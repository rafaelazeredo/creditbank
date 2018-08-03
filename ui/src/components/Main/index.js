import * as React from 'react'
import {NodeApi} from '../../service/httpservice'

export default class Main extends React.Component{

    nodeASource= "http://localhost:8081/"
    
    constructor(props){
        super(props)
        this.state = {
            transactions: []
        }
    }

    async getTransactions(){
        var transactions= [];

        var nodeA = new NodeApi(this.nodeASource);

        for (var i=0; i<50; i++){
            var stuffToAdd = await nodeA.getLastTransaction();
            transactions.push(stuffToAdd);
        }

        return transactions;
    }

    async componentDidMount(){
        var transactions = await this.getTransactions();
        this.setState({ transactions: transactions});
    }

    render(){
        const listItems = this.state.transactions.map(t => <li>{t.amount}</li> )
        debugger
        return (<ul>{listItems}</ul>)
    }
}