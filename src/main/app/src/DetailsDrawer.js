import React, {useContext, useState} from 'react';
import {makeStyles} from "@material-ui/core/styles";
import Fab from '@material-ui/core/Fab';
import SearchIcon from '@material-ui/icons/Search';
import ResultsIcon from '@material-ui/icons/List';
import ChartIcon from '@material-ui/icons/BarChart';
import Drawer from "@material-ui/core/Drawer";
import VirtualList from "./VirtualList";
import Button from "@material-ui/core/Button";
import TabBar from "./TabBar";
import HistoryList from "./HistoryList";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import HistoryChart from "./HistoryChart";
import {graphTitle} from "./selectionProperties";
import {MapContext} from "./MapContext";


const useStyles = makeStyles((theme) => ({
    root: {
        background: 'transparent'
    },
    drawer: {

    },
    contentRoot: {
        height: '100%'
    },
    toggleBtn: {
        position: 'absolute',
        right: 0,
        padding: '50px'
    },
    fab: {

    },
}));

export default function DetailsDrawer(props) {
    const classes = useStyles();
    const model = useContext(MapContext);
    const [open, setOpen] = useState(false);
    const [openChart, setOpenChart] = useState(false);

    const [tab, setTab] = useState(0);

    function handleOpenVertices() {
        setOpen(true);
        setTab(0);
    }

    function handleClose() {
        setOpen(false)
    }

    function handleOpenChart() {
        setOpenChart(true)
    }

    function handleCloseChart() {
        setOpenChart(false)
    }

    function handleOpenHistory() {
        setOpen(true);
        setTab(1);
    }


    return (
        <div className={classes.root}>
            <div className={classes.toggleBtn}>
                <Fab className={classes.fab} color="primary" onClick={handleOpenVertices}>
                    <SearchIcon />
                </Fab>

                <Fab className={classes.fab} color="primary" onClick={handleOpenHistory}>
                    <ResultsIcon />
                </Fab>

                <Fab className={classes.fab} color="primary" onClick={handleOpenChart}>
                    <ChartIcon />
                </Fab>
            </div>
            <Drawer className={classes.drawer} open={open}  onClose={handleClose} anchor='right' variant="temporary">
                <div className={classes.contentRoot}>
                    <TabBar tab={tab} onChange={setTab} tabs={[
                        {
                            label: 'Vertices',
                            icon: <SearchIcon />
                        },
                        {
                            label: 'History',
                            icon: <ResultsIcon />
                        }
                    ]}/>

                    <div style={{marginTop: '2px'}}>
                        {tab === 0 ? <VirtualList /> : null}
                        {tab === 1 ? <HistoryList /> : null}
                    </div>
                </div>
                <Button variant="outlined" color="secondary" onClick={handleClose}>Close</Button>
            </Drawer>

            <Dialog onClose={handleCloseChart} aria-labelledby="customized-dialog-title" open={openChart}>
                <DialogTitle id="customized-dialog-title" onClose={handleCloseChart}>
                    Data chart for {graphTitle(model.selectedGraph)}
                </DialogTitle>
                <DialogContent dividers>
                    <HistoryChart />
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={handleCloseChart} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}