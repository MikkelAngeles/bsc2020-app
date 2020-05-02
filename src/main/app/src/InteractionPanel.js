import React, {useContext, useEffect, useState} from 'react';
import TextField from "@material-ui/core/TextField";
import Autocomplete from '@material-ui/lab/Autocomplete';
import IconButton from "@material-ui/core/IconButton";
import Divider from "@material-ui/core/Divider";
import DirectionsIcon from "@material-ui/icons/Directions";
import Paper from "@material-ui/core/Paper";
import {makeStyles} from "@material-ui/core/styles";
import SettingsSwitch from "./SettingsSwitch";
import {MapContext} from "./MapContext";
import TabBar from "./TabBar";
import SelectFileDialog from "./SelectFileDialog";
import SelectAlgorithmDialog from "./SelectAlgorithmDialog";
import DetailsView from "./DetailsView";
import {SelectionContext} from "./SelectionContext";

const useStyles = makeStyles((theme) => ({
    root: {
        padding: '10px 20px 10px 20px',
        display: 'flex',
        alignItems: 'center',
        width: 360,
        boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12);'
    },
    input: {
        marginLeft: theme.spacing(1),
        flex: 1,
    },
    iconButton: {
        padding: 10,
    },
    divider: {
        height: 28,
        margin: 4,
    },
    ppr: {
        boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12)',
    }
}));

export default function InteractionPanel (props) {
    const mapModel = useContext(MapContext);
    const selModel = useContext(SelectionContext);

    const {onLonSearch1, onLatSearch1, onLonSearch2, onLatSearch2, lonSearchResults} = props;

    const [tab, setTab] = useState(0);
    const classes = useStyles();
    function handleLonSearch1(e) {
        onLonSearch1(e.currentTarget.value);
    }
    function handleLatSearch1(e) {
        onLatSearch1(e.currentTarget.value);
    }
    function handleLonSearch2(e) {
        onLonSearch2(e.currentTarget.value);
    }
    function handleLatSearch2(e) {
        onLatSearch2(e.currentTarget.value);
    }

    function handleOnClickCalculate() {
        mapModel.calculateRoute();
    }

    useEffect(() => {
        console.log(lonSearchResults);
    }, [lonSearchResults]);

    return (
        <div>


            <Paper component="form" className={classes.root}>
                <div style={{display: 'flex', height: 114, width: '100%', padding: '10px'}}>
                    <div style={{width: '100%', marginRight: '20px'}}>
                        <Autocomplete
                            id="lon1"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%', marginBottom: '5px'}}
                            renderInput={(params) => (
                                <TextField {...params}  value={selModel.selectedPoints.routeFrom.point[0]} id="standard-basic" label="Longitude from" onChange={handleLonSearch1} />
                            )}
                        />

                        <Autocomplete
                            id="lat1"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%'}}
                            renderInput={(params) => (
                                <TextField {...params} value={selModel.selectedPoints.routeFrom.point[1]} id="standard-basic" label="Latitude from" onChange={handleLatSearch1} />
                            )}
                        />
                    </div>

                    <div style={{width: '100%'}}>
                        <Autocomplete
                            id="lon2"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%', marginBottom: '5px'}}
                            renderInput={(params) => (
                                <TextField {...params} value={selModel.selectedPoints.routeTo.point[0]} id="standard-basic" label="Longitude to" onChange={handleLonSearch2} />
                            )}
                        />

                        <Autocomplete
                            id="lat2"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%'}}
                            renderInput={(params) => (
                                <TextField {...params} value={selModel.selectedPoints.routeTo.point[1]} id="standard-basic" label="Latitude to" onChange={handleLatSearch2} />
                            )}
                        />
                    </div>
                </div>

                <Divider className={classes.divider} orientation="vertical" />
                <IconButton color="primary" className={classes.iconButton} aria-label="directions" onClick={handleOnClickCalculate}>
                    <DirectionsIcon />
                </IconButton>
            </Paper>

            <Paper className={classes.ppr}>
                <div style={{display: 'flex', marginTop: '20px', justifyContent: 'center', padding: '15px 0 15px 0', borderBottom: '1px solid #ccc'}}>
                    <SelectFileDialog  />
                    <SelectAlgorithmDialog />
                </div>
                <div style={{overflow: 'auto', height: '400px', padding: '0px 10px 10px 10px', boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12);'}}>
                    {tab === 0 ?
                    <SettingsSwitch/> : <DetailsView />
                    }
                </div>

                <TabBar tab={tab} onChange={setTab} />
            </Paper>
        </div>
    );
}