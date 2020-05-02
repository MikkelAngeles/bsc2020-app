import React, {useContext, useState} from 'react';
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
import { blue } from '@material-ui/core/colors';
import {SelectionContext} from "./SelectionContext";
import {MapContext} from "./MapContext";
import {algorithms, algorithmTitle} from "./selectionProperties";



const useStyles = makeStyles({
    avatar: {
        backgroundColor: blue[100],
        color: blue[600],
    },
});

function SimpleDialog(props) {
    const classes = useStyles();
    const { onClose,  open } = props;
    const model = useContext(MapContext);
    const algs = algorithms;
    const selected = model.selectedAlgorithm;
    const handleClose = () => {
        onClose();
    };

    const handleListItemClick = (value) => {
        model.setSelectedAlgorithm(value);
        onClose();
    };

    return (
        <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
            <DialogTitle id="simple-dialog-title">Select Algorithm</DialogTitle>
            <List>
                {algs.map((f,k) => (
                    <ListItem button onClick={() => handleListItemClick(f.id)} key={f.id} selected={selected === f.id}>
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
};

export default function SelectAlgorithmDialog(props) {
    const [open, setOpen] = useState(false);
    const model = useContext(MapContext);
    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <div>
            <Button variant="outlined" color="primary" onClick={handleClickOpen}>
                {algorithmTitle(model.selectedAlgorithm)}
            </Button>
            <SimpleDialog open={open} onClose={handleClose} />
        </div>
    );
}
