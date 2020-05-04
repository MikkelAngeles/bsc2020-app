import React, {useContext} from 'react';

import { makeStyles } from '@material-ui/core/styles';
import ListItem from '@material-ui/core/ListItem';
import {MapContext} from "./MapContext";
import Avatar from "@material-ui/core/Avatar";
import List from "@material-ui/core/List";
import Typography from "@material-ui/core/Typography";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemText from "@material-ui/core/ListItemText";

const useStyles = makeStyles((theme) => ({
    root: {
        backgroundColor: theme.palette.background.paper,
        display: 'flex',
        flexDirection: 'column',
        padding: '20px'
    },
    fixedList: {
        height: '100%'
    }
}));

export default function HistoryList(props) {
    const classes = useStyles();
    const model = useContext(MapContext);
    let data    = model.results;

    function RenderRow(props) {
        const {item, index} = props;

        return (
            <ListItem key={index}>
                <ListItemAvatar>
                    <Avatar>
                        {item.algorithm.substring(0, 1)}
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary={item.algorithm + " ("+ (item.data.elapsed / 1000000).toFixed(0) + " ms)"} secondary={
                    <>
                        {
                            item.data.hasPath ?
                                "Dist " + item.data.dist.toFixed(4) +
                                " visited " + item.data.visited.length +
                                " route length " + item.data.route.length
                                :
                                "No path"
                        }
                    <div>
                        {"From " + item.from + " to " + item.to}
                    </div>
                    </>
                } />
            </ListItem>
        );
    }

    return (
        <div className={classes.root}>
            <Typography variant="h5" gutterBottom>
                History
            </Typography>
            <Typography variant="subtitle1" gutterBottom>
                Displaying {data.length} results
            </Typography>
            <div className={classes.list}>
                <List>
                    {data.map((v, k) =>
                        <RenderRow
                            item = {v}
                            index = {k}
                        />
                    )}
                </List>
            </div>
        </div>
    );
}