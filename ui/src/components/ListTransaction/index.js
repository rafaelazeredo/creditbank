import * as React from 'react';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import { renderComponent } from 'recompose';

export default class ListTransactions extends React.Component {
    

    render(){
        const list = this.props.transactions.map(t => 
            <TableRow>
                 <TableCell>{t.nino}</TableCell>
                    <TableCell>{t.date.toLocaleDateString('en-UK')}</TableCell>
                    <TableCell>{t.accountType}</TableCell>
                    <TableCell>{t.amount}</TableCell>
                    <TableCell>{t.description}</TableCell>
                    <TableCell>{t.debit}</TableCell>
            </TableRow> )
        var table = (
        <Table>
            <TableHead>
                <TableRow>
                    <TableCell>National Insurance No.</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Account Type</TableCell>
                    <TableCell>Amount</TableCell>
                    <TableCell>Description</TableCell>
                    <TableCell>Debit</TableCell>
                </TableRow>
            </TableHead>
            {list}
        </Table>)
        return ( 
            table
        )
    }
}