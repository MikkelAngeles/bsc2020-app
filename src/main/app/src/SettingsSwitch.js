import React, {useContext} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import Switch from '@material-ui/core/Switch';
import WeightIcon from '@material-ui/icons/FitnessCenter';

import AspectIcon from '@material-ui/icons/AspectRatio';

import DirectionsIcon from '@material-ui/icons/Directions';
import MapIcon from '@material-ui/icons/Map';
import PlotIcon from '@material-ui/icons/ScatterPlot';

import LandmarksFlag from '@material-ui/icons/OutlinedFlag';
import {SelectionContext} from "./SelectionContext";
import TextField from "@material-ui/core/TextField";

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper,
        textAlign: 'left'
    },
}));

function SwitchItem(props) {
    const {primary, onChange, checked, icon} = props;
    return(
        <ListItem>
            <ListItemIcon>
                {icon}
            </ListItemIcon>
            <ListItemText id={`switch-list-label-${primary}`} primary={primary} />
            <ListItemSecondaryAction>
                <Switch
                    edge        = "end"
                    onChange    = {onChange}
                    checked     = {checked}
                    inputProps  = {{ 'aria-labelledby': `switch-list-label-${primary}`}}
                    color       = "primary"
                />
            </ListItemSecondaryAction>
        </ListItem>
    )
}

export default function SettingsSwitch() {
    const classes = useStyles();
    const model = useContext(SelectionContext);
    let checked = model.checked;

    const handleToggle = (value) => () => {
        const currentIndex = checked.indexOf(value);
        const newChecked = [...checked];

        if (currentIndex === -1) newChecked.push(value);
        else newChecked.splice(currentIndex, 1);
        model.setChecked(newChecked);
    };

    return (
        <>
            <List subheader={<ListSubheader>Route settings</ListSubheader>} className={classes.root}>
                <SwitchItem
                    primary     = "Route"
                    onChange    = {handleToggle('route')}
                    checked     = {checked.indexOf('route') !== -1}
                    icon        = {<DirectionsIcon />}
                />
                <SwitchItem
                    primary     = "Visited"
                    onChange    = {handleToggle('visited')}
                    checked     = {checked.indexOf('visited') !== -1}
                    icon        = {<PlotIcon />}
                />
                <SwitchItem
                    primary     = "Visited region"
                    onChange    = {handleToggle('visitedHull')}
                    checked     = {checked.indexOf('visitedHull') !== -1}
                    icon        = {<MapIcon />}
                />
            </List>
            <List subheader={<ListSubheader>Graph settings </ListSubheader>} className={classes.root}>
                <SwitchItem
                    primary     = "Graph bounds"
                    onChange    = {handleToggle('graphBounds')}
                    checked     = {checked.indexOf('graphBounds') !== -1}
                    icon        = {<AspectIcon />}
                />

                <SwitchItem
                    primary     = "Vertices"
                    onChange    = {handleToggle('vertices')}
                    checked     = {checked.indexOf('vertices') !== -1}
                    icon        = {<PlotIcon />}
                />

                <SwitchItem
                    primary     = "Hull"
                    onChange    = {handleToggle('verticesHull')}
                    checked     = {checked.indexOf('verticesHull') !== -1}
                    icon        = {<MapIcon />}
                />
            </List>
            <List subheader={<ListSubheader>Algorithm settings</ListSubheader>} className={classes.root}>
                <SwitchItem
                    primary     = "Landmarks"
                    onChange    = {handleToggle('landmarks')}
                    checked     = {checked.indexOf('landmarks') !== -1}
                    icon        = {<LandmarksFlag />}
                />
                <SwitchItem
                    primary     = "Landmarks hull"
                    onChange    = {handleToggle('landmarksHull')}
                    checked     = {checked.indexOf('landmarksHull') !== -1}
                    icon        = {<MapIcon />}
                />
                <ListItem>
                    <ListItemIcon>
                        <WeightIcon />
                    </ListItemIcon>
                    <ListItemText id={`list-input-label-heuristic-weight`} primary="Heuristics weight" />
                    <ListItemSecondaryAction>
                        <div style={{width: '50px'}}>
                            <TextField
                                id          = "standard-basic"
                                value       = {model.heuristicsWeight}
                                onChange    = {(event => model.setHeuristicsWeight(event.currentTarget.value))}
                            />
                        </div>
                    </ListItemSecondaryAction>
                </ListItem>
            </List>
        </>
    );
}
