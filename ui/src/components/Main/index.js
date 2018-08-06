import * as React from 'react'
import {NodeApi} from '../../service/httpservice'

import ListTransactions from '../ListTransaction';
import { Paper } from '@material-ui/core';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';


// import { withStyles } from '@material-ui/core/styles';

const searchContainer = {
    width: 1000, 
    padding: 10,
    marginTop: 20
}
const transactionListStyle = {
    width: 1000, 
    padding: 10,
    margin: "0 auto",
    marginTop: 20
}

const ninoSearchFieldStyle = {
    width: 300
}

const searchButtonStyle = {
    marginLeft: 16
}

export default class Main extends React.Component{

    nodeASource= "http://localhost:8081/"
    
    constructor(props){
        super(props)
        this.state = {
            transactions: [],
            nino: '',
            searchText: ''
        }
    }   

    handleNinoSearch = (nino) => {
            this.setState({nino: this.state.searchText})
    }

    handleChange = (e) => {
        this.setState({searchText: e.target.value || ''})
    }

    async getTransactions(){

        var nodeA = new NodeApi(this.nodeASource);

        var stuffToAdd = await nodeA.getTransactions();
        return stuffToAdd;
    }

    async componentDidMount(){
        var transactions = await this.getTransactions();
        this.setState({ transactions});
    }

    render(){
        
        const transactionToShow =  this.state.transactions.filter(t=>t.nino===this.state.nino) ;
        return (
        <div className="main">
            <Paper style = {transactionListStyle}>
            <h3 style={{display: "block", textAlign:"left"}}>Search for credit data in other financial institutions on the network</h3>
           <br/>

            <TextField
                id="name"
                style={ninoSearchFieldStyle}
                placeholder="Search National Insurance No."
                value={this.state.searchText}
                onChange={this.handleChange}
                margin="normal"
                />
             <Button variant="contained" color="primary" size="small" style={searchButtonStyle} onClick={this.handleNinoSearch} >
                Search 
            </Button>
            </Paper>
            <Paper style = {transactionListStyle}>
                <ListTransactions transactions={transactionToShow}/>
            </Paper>
        </div>);
    }
}