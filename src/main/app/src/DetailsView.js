import React, {useContext} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import WeightIcon from '@material-ui/icons/FitnessCenter';
import AspectIcon from '@material-ui/icons/AspectRatio';
import DirectionsIcon from '@material-ui/icons/Directions';
import PlotIcon from '@material-ui/icons/ScatterPlot';
import InfoIcon from '@material-ui/icons/Info';
import LandmarksFlag from '@material-ui/icons/OutlinedFlag';
import {MapContext} from "./MapContext";
import {algorithmTitle} from "./selectionProperties";

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper,
        textAlign: 'left'
    },
}));

function DetailsItem(props) {
    const {primary, value, icon} = props;
    return(
        <ListItem>
            <ListItemIcon>
                {icon}
            </ListItemIcon>
            <ListItemText id={`switch-list-label-${primary}`} primary={primary} />
            <ListItemSecondaryAction>
                <div style={{
                    width: '100px',
                    overflow: 'hidden',
                    maxHeight: '42px',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap'
                }}>
                    {value}
                </div>
            </ListItemSecondaryAction>
        </ListItem>
    )
}

export default function DetailsView() {
    const classes = useStyles();
    const model = useContext(MapContext);
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
                <DetailsItem
                    primary     = "Route"
                    value       = {model.route.route.length}
                    icon        = {<DirectionsIcon />}
                />
                <DetailsItem
                    primary     = "Distance"
                    value       = {model.route.dist}
                    icon        = {<PlotIcon />}
                />
                <DetailsItem
                    primary     = "Has path"
                    value       = {model.route.hasPath ? "Yes" : "No"}
                    icon        = {<PlotIcon />}
                />
            </List>
            <List subheader={<ListSubheader>Graph settings</ListSubheader>} className={classes.root}>
                <DetailsItem
                    primary     = "Name"
                    value       = {model.selectedGraph}
                    icon        = {<InfoIcon />}
                />
                <DetailsItem
                    primary     = "Bounds"
                    value       = {"[]"}
                    icon        = {<AspectIcon />}
                />

                <DetailsItem
                    primary     = "Vertices"
                    value       = {model.graph.V}
                    icon        = {<PlotIcon />}
                />
                <DetailsItem
                    primary     = "Edges"
                    value       = {model.graph.E}
                    icon        = {<PlotIcon />}
                />
            </List>
            <List subheader={<ListSubheader>Algorithm settings</ListSubheader>} className={classes.root}>
                <DetailsItem
                    primary     = "Name"
                    value       = {algorithmTitle(model.selectedAlgorithm)}
                    icon        = {<InfoIcon />}
                />
                <DetailsItem
                    primary     = "Elapsed (ms)"
                    value       = {model.route.elapsed / 1000000}
                    icon        = {<PlotIcon />}
                />
                <DetailsItem
                    primary     = "Visits"
                    value       = {model.route.visited.length}
                    icon        = {<PlotIcon />}
                />
                <DetailsItem
                    primary     = "Landmarks"
                    value       = {model.graph.landmarks.length}
                    icon        = {<LandmarksFlag />}
                />
                <DetailsItem
                    primary     = "Heuristic weight"
                    value       = {1}
                    icon        = {<WeightIcon />}
                />
            </List>
        </>
    );
}
