import React, {useEffect} from 'react';
import Button from "@material-ui/core/Button";
import SearchPanel from "./SearchPanel";
import InputBase from "@material-ui/core/InputBase";
import TextField from "@material-ui/core/TextField";
import Autocomplete from '@material-ui/lab/Autocomplete';
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";
import SearchIcon from "@material-ui/icons/Search";
import Divider from "@material-ui/core/Divider";
import DirectionsIcon from "@material-ui/icons/Directions";
import Paper from "@material-ui/core/Paper";
import {makeStyles} from "@material-ui/core/styles";
const useStyles = makeStyles((theme) => ({
    root: {
        padding: '2px 4px',
        display: 'flex',
        alignItems: 'center',
        width: 400,
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
}));

export default function InteractionPanel (props) {
    const {toggleGraph, onGraphToggle, toggleLandmarks, onToggleLandmarks, toggleVisited, onToggleVisited} = props;
    const {onLonSearch1, onLatSearch1, onLonSearch2, onLatSearch2, lonSearchResults, submit} = props;
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
        submit();
    }

    useEffect(() => {
        console.log(lonSearchResults);
    }, [lonSearchResults]);

    return (
        <div>
            <Typography variant="h5" component="h2">
                Shortest Path Visualization
            </Typography>
            <Typography variant="subtitle2" component="h2" gutterBottom>
                Author: Mikkel Helmersen, mhel@itu.dk
            </Typography>

            <Paper component="form" className={classes.root}>
                <div style={{display: 'flex', height: 114, width: '100%', padding: '10px'}}>
                    <div style={{width: '100%', marginRight: '20px'}}>
                        <Autocomplete
                            id="lon1"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%', marginBottom: '5px'}}
                            renderInput={(params) => (
                                <TextField {...params}  id="standard-basic" label="Longitude from" onChange={handleLonSearch1} />
                            )}
                        />

                        <Autocomplete
                            id="lat1"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%'}}
                            renderInput={(params) => (
                                <TextField {...params} id="standard-basic" label="Latitude from" onChange={handleLatSearch1} />
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
                                <TextField {...params} id="standard-basic" label="Longitude to" onChange={handleLonSearch2} />
                            )}
                        />

                        <Autocomplete
                            id="lat2"
                            freeSolo
                            options={lonSearchResults.map(val => val.toString())}
                            style={{width: '100%'}}
                            renderInput={(params) => (
                                <TextField {...params} id="standard-basic" label="Latitude to" onChange={handleLatSearch2} />
                            )}
                        />
                    </div>
                </div>

                <Divider className={classes.divider} orientation="vertical" />
                <IconButton color="primary" className={classes.iconButton} aria-label="directions" onClick={handleOnClickCalculate}>
                    <DirectionsIcon />
                </IconButton>
            </Paper>

            <div style={{display: 'flex', flexDirection: 'column', width: '100%', marginTop: '20px'}}>
                <Button variant={toggleGraph ? "contained" : "outlined"} color="primary" onClick={onGraphToggle} style={{marginBottom: '20px' }}>
                    Toggle graph
                </Button>
                <Button variant={toggleVisited ? "contained" : "outlined"} color="primary" onClick={onToggleVisited}>
                    Toggle visited vertices
                </Button>
            </div>

            <div>
                <div>Graph details</div>
                <p>Vertices: 0</p>
                <p>Edges: 0</p>
                <p>Landmarks: 0</p>
            </div>
        </div>
    );
}