import React, {useContext, useState} from 'react';
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
import Chip from "@material-ui/core/Chip";
import Avatar from "@material-ui/core/Avatar";
import FromIcon from "@material-ui/icons/Flag";
import ListItem from "@material-ui/core/ListItem";
import LocateIcon from "@material-ui/icons/MyLocation";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import List from "@material-ui/core/List";
import Typography from "@material-ui/core/Typography";
import ResultsIcon from '@material-ui/icons/Description';
import SettingsIcon from '@material-ui/icons/Settings';

function PointChip(props) {
    const {item} = props;
    return(
        <div style={{display: 'flex', flexDirection: 'row', alignItems: 'center', width: '310px'}}>
            <Chip variant="outlined" label={item.index}/>
            <Chip variant="outlined" avatar={<Avatar>Lon</Avatar>} label={`${item.point[0]}`} style={{justifyContent: 'start', width: '100%'}} />
            <Chip variant="outlined" avatar={<Avatar>Lat</Avatar>} label={`${item.point[1]}`} style={{justifyContent: 'start', width: '100%'}}/>
        </div>
    )
}

const useStyles = makeStyles((theme) => ({
    root: {
        //padding: '10px 20px 10px 20px',
        display: 'flex',
        alignItems: 'center',
        width: 440,
        boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12);'
    },
    input: {
        marginLeft: theme.spacing(1),
        flex: 1,
    },
    iconButton: {
        padding: 10,
        width: '50px'
    },
    divider: {
        width: '100%',
        margin: 4,
    },
    ppr: {
        boxShadow: '0px 0px 4px 1px rgba(0,0,0,0.2), 0px 1px 1px 0px rgba(0,0,0,0.14), 0px 1px 3px 0px rgba(0,0,0,0.12)',
    }
}));

export default function InteractionPanel (props) {
    const mapModel = useContext(MapContext);
    const selModel = useContext(SelectionContext);

    const [tab, setTab] = useState(0);
    const classes = useStyles();

    function handleOnClickCalculate() {
        mapModel.calculateRoute();
    }

    return (
        <div>


            <Paper component="form" className={classes.root}>

                <div style={{display: 'flex', width: '100%', flexDirection: 'column', alignItems: 'center', paddingTop: '10px'}}>
                    <Typography variant="h5" component="h2">
                        Shortest Path Visualization
                    </Typography>

                    <Typography variant="subtitle2" component="h2">
                        A bachelor project by Mikkel Helmersen
                    </Typography>

                    <Divider className={classes.divider} orientation="horizontal" />
                    <List>
                        <ListItem>
                            <ListItemAvatar>
                                <Avatar style={{background: 'green'}}>
                                    <FromIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <PointChip item={selModel.selectedPoints.routeFrom}/>
                        </ListItem>

                        <ListItem>
                            <ListItemAvatar>
                                <Avatar style={{background: 'red'}}>
                                    <FromIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <PointChip item={selModel.selectedPoints.routeTo}/>
                        </ListItem>

                        <ListItem>
                            <ListItemAvatar>
                                <Avatar style={{background: 'blue'}}>
                                    <LocateIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <PointChip item={selModel.selectedPoints.nearest}/>
                        </ListItem>

                    </List>

                    <Divider className={classes.divider} orientation="horizontal" />
                    <IconButton color="primary" className={classes.iconButton} aria-label="directions" onClick={handleOnClickCalculate}>
                        <DirectionsIcon />
                    </IconButton>
                </div>


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

                <TabBar tab={tab} onChange={setTab} tabs={[
                    {
                        label: 'Settings',
                        icon: <SettingsIcon />
                    },
                    {
                        label: 'Details',
                        icon: <ResultsIcon />
                    }
                ]}/>

            </Paper>
        </div>
    );
}