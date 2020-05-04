import React, {useState} from 'react';
import {makeStyles} from "@material-ui/core/styles";
import Fab from '@material-ui/core/Fab';
import SearchIcon from '@material-ui/icons/Search';
import Drawer from "@material-ui/core/Drawer";
import VirtualList from "./VirtualList";
import Button from "@material-ui/core/Button";

const useStyles = makeStyles((theme) => ({
    root: {
        background: 'transparent'
    },
    drawer: {

    },
    contentRoot: {
        padding: '20px',
        height: '100%'
    },
    toggleBtn: {
        position: 'absolute',
        right: 0,
        padding: '50px'
    },
    fab: {
        position: 'absolute',
        bottom: theme.spacing(2),
        right: theme.spacing(2),
    },
}));

export default function DetailsDrawer(props) {
    const classes = useStyles();
    const [open, setOpen] = useState(false);

    function handleOpen() {
        setOpen(true)
    }

    function handleClose() {
        setOpen(false)
    }

    return (
        <div className={classes.root}>
            <div className={classes.toggleBtn} onClick={handleOpen}>
                <Fab className={classes.fab} color="primary">
                    <SearchIcon />
                </Fab>
            </div>
            <Drawer className={classes.drawer} open={open}  onClose={handleClose} anchor='right' variant="temporary">
                <div className={classes.contentRoot}>
                    <VirtualList />
                </div>
                <Button onClick={handleClose}>Close</Button>
            </Drawer>
        </div>
    )
}