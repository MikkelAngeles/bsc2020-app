import React from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Avatar from '@material-ui/core/Avatar';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import PersonIcon from '@material-ui/icons/Person';
import AddIcon from '@material-ui/icons/Add';
import Typography from '@material-ui/core/Typography';
import { blue } from '@material-ui/core/colors';

const algorithms = [
    {
        id: 0,
        file: 'Dijkstra',
        type: 'Mikkel Helmersen',
    },
    {
        id: 1,
        file: 'A*',
        type: 'Mikkel Helmersen',
    },
    {
        id: 2,
        file: 'A* with landmarks',
        type: 'Mikkel Helmersen',
    },
    {
        id: 3,
        file: 'Dijkstra',
        type: 'https://algs4.cs.princeton.edu/',
    },
    {
        id: 4,
        file: 'A*',
        type: 'https://algs4.cs.princeton.edu/',
    },
];

const useStyles = makeStyles({
    avatar: {
        backgroundColor: blue[100],
        color: blue[600],
    },
});

function SimpleDialog(props) {
    const classes = useStyles();
    const { onClose, selectedValue, open } = props;

    const handleClose = () => {
        onClose(selectedValue);
    };

    const handleListItemClick = (value) => {
        onClose(value);
    };

    return (
        <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
            <DialogTitle id="simple-dialog-title">Select Algorithm</DialogTitle>
            <List>
                {algorithms.map((f,k) => (
                    <ListItem button onClick={() => handleListItemClick(f.id)} key={f.id}>
                        <ListItemAvatar>
                            <Avatar className={classes.avatar}>
                                <PersonIcon />
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={f.file} secondary={"Author: " + f.type } />
                    </ListItem>
                ))}

            </List>
        </Dialog>
    );
}

SimpleDialog.propTypes = {
    onClose: PropTypes.func.isRequired,
    open: PropTypes.bool.isRequired,
    selectedValue: PropTypes.string.isRequired,
};

export default function SelectAlgorithmDialog(props) {
    const [open, setOpen] = React.useState(false);
    const {selected, onSelect} = props;

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (value) => {
        if(value === 6) {

        }
        setOpen(false);
        onSelect(value);
    };

    return (
        <div>
            <Button variant="outlined" color="primary" onClick={handleClickOpen}>
                Select Algorithm
            </Button>
            <SimpleDialog selectedValue={selected} open={open} onClose={handleClose} />
        </div>
    );
}
