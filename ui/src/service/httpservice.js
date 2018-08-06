import * as randomize from 'randomatic'

export class NodeApi{

    baseUrl;

    constructor(url){
        this.baseUrl=url
    }

    addTransaction(transaction){

        
    }

    async getTransactions(){
        const transactions = Array.apply(null, {length: 150}).map(Number.call, Number);
        const demoNino = "VW374751O";
        const mappedTransactions = transactions.map( (t)=> {
            const type = parseInt(Math.random()*4);
            return {
                nino: Math.random() > 0.1 ? randomize("A", 2) + randomize("0",6) + randomize("A", 1) : demoNino,
                date: new Date( (parseInt(Math.random()*10) +2008) , parseInt(Math.random()*12 + 1) , parseInt(Math.random()*28) ),
                accountType: getAccountType(type),
                amount: "£"+(Math.random()*977).toFixed(2),
                description: getAccountDescription(type),
                debit: (type === 0) ? 'YES': 'NO'
            }
        })
        
        return Promise.resolve(mappedTransactions)
        // fetch(this.baseUrl+"credit/addtransaction").then( (response)=>{
        // })
    }

    async getLastTransaction(){

        var stuff = await fetch(this.baseUrl+"credit/lasttransaction");
        return await stuff.json();
    }
}

const getAccountType = (i)=>{
    switch(parseInt(i)){
        case 0 :  return "DEBIT";
        case 1 : return "CREDIT";
        case 2: return "AUTOLOAN";
        default: return "MORTGAGE"
    }
}

const getAccountDescription = (i)=>{
    const evenMoreRandom = parseInt(Math.random()*2);
    switch(i){
        case 0: return debitDescription[evenMoreRandom] ;
        case 1: return creditDescription[evenMoreRandom] ;
        case 2: return autoloanDescription[evenMoreRandom];
        default: return mortgageDescription[evenMoreRandom];
    }
}

const debitDescription = ['TV bill', 'Phone bill'];
const creditDescription = ['Car rental', 'Declined for Barclays credit card'];
const autoloanDescription = ['Loan repayment', 'Late repayment fees'];
const mortgageDescription = ['Mortgage repayment', 'Declined for mortgage'];