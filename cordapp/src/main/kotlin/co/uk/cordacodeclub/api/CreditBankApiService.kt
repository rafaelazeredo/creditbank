package co.uk.cordacodeclub.api

import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.loggerFor
import io.bluebank.braid.corda.BraidConfig
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.bluebank.braid.corda.rest.RestConfig
import io.vertx.core.http.HttpServerOptions

const val BRAID_CONFIG_FILENAME = "braid-config.json"
const val BRAID_DISABLED_PORT = -1
private const val BRAID_PORT_FIELD = "port"

@CordaService
class CreditBankApiService(serviceHub : AppServiceHub) : SingletonSerializeAsToken() {

  companion object {
    private val log = loggerFor<CreditBankApiService>()
  }

  private val org = serviceHub.myInfo.legalIdentities.first().name.organisation.replace(" ", "")

  private val config: JsonObject by lazy {
    try {
      JsonObject(Resources.loadResourceAsString(BRAID_CONFIG_FILENAME)).let {
        log.info("found $BRAID_CONFIG_FILENAME")
        it
      }
    } catch (_: Throwable) {
      log.warn("could not find braid config $BRAID_CONFIG_FILENAME")
      JsonObject()
    }
  }

  private val baseConfig: BraidConfig by lazy {
    val json = if (config.containsKey(org)) {
      log.info("found config for org $org")
      config.getJsonObject(org)
    } else {
      log.warn("cannot find braid config for $org")
      JsonObject().put("port", BRAID_DISABLED_PORT)
    }

    val overridePort = getBraidPortFromEnvironment()
    when {
      overridePort != null -> json.put(BRAID_PORT_FIELD, overridePort)
      json.containsKey(BRAID_PORT_FIELD) -> try {
        json.getInteger(BRAID_PORT_FIELD)
      } catch (_: Throwable) {
        log.error("'$BRAID_PORT_FIELD' is not an int. default to $BRAID_DISABLED_PORT")
        json.put(BRAID_PORT_FIELD, BRAID_DISABLED_PORT)
      }
      else -> {
        log.error("no port provided for $org in config nor environment variable. defaulting to $BRAID_DISABLED_PORT ")
        json.put(BRAID_PORT_FIELD, BRAID_DISABLED_PORT)
      }
    }
    Json.decodeValue(json.toString(), BraidConfig::class.java)
  }

  init {
    log.info("starting $org braid on port ${baseConfig.port}")
    val creditBankApi = CreditbankApiImpl(serviceHub)

    if (baseConfig.port != BRAID_DISABLED_PORT) {
      baseConfig
        .withService("credit", creditBankApi)
        .mountRestEndPoints(creditBankApi)
        .withHttpServerOptions(HttpServerOptions().setSsl(false))
        .bootstrapBraid(serviceHub)
    } else {
      log.info("no braid configuration nor environment variable for node $org")
      log.info("not starting braid for node $org")
    }
  }

  private fun BraidConfig.mountRestEndPoints(creditApi: CreditBankApi): BraidConfig {
    return this.withRestConfig(
      RestConfig(
        serviceName = "credit",
        description = "REST API for accessing our awesome credit scoring app",
        hostAndPortUri = "http://localhost:${baseConfig.port}",
        apiPath = "/credit") {
        group("credit") {
          get("/lasttransaction", creditApi::getLastTransaction)
          post("/addtransaction", creditApi::addTransaction)
        }
      }
    )
  }

  private fun getBraidPortFromEnvironment(): Int? {
    val property = "braid.$org.port"
    return getProperty(property)?.toInt()
  }

  private fun getProperty(propertyName: String, default: String? = null): String? {
    val property = System.getProperty(propertyName)
    if (property != null) {
      log.info("found property: $property with value $property")
      return property
    }
    val envVariableName = propertyName.replace('.', '_').toUpperCase()

    val env = System.getenv(envVariableName)
    if (env != null) {
      log.info("found env variable: $envVariableName with value $env")
      return env
    }
    log.info("could not find property $propertyName nor environment variable $envVariableName. defaulting to $default")
    return default
  }
}

object Resources {
  @Throws(IllegalArgumentException::class)
  fun loadResourceAsString(path: String): String {
    val stream = ClassLoader.getSystemClassLoader().getResourceAsStream(path)
      ?: throw IllegalArgumentException("could not locate resource $path")

    return stream.use {
      val scanner = java.util.Scanner(stream).useDelimiter("\\A")
      if (scanner.hasNext()) scanner.next() else ""
    }
  }
}

