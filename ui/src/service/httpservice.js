
export class NodeApi{

    baseUrl;

    constructor(url){
        this.baseUrl=url
    }

     addTransaction(transaction){
         fetch(this.baseUrl+"credit/addtransaction").then( (response)=>{
         })
    }

    async getLastTransaction(){

        var stuff = await fetch(this.baseUrl+"credit/lasttransaction");

        return await stuff.json();
    }
}