import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import ResultsIcon from '@material-ui/icons/Description';
import SettingsIcon from '@material-ui/icons/Settings';

const useStyles = makeStyles({
    root: {
        flexGrow: 1,
        maxWidth: 500,
        borderTop: '1px solid #ccc'
    },
});

export default function TabBar(props) {
    const classes = useStyles();
    const {onChange, tab} = props;

    const handleChange = (event, newValue) => {
        onChange(newValue);
    };

    return (
        <Paper square className={classes.root}>
            <Tabs
                value           = {tab}
                onChange        = {handleChange}
                variant         = "fullWidth"
                indicatorColor  = "primary"
                textColor       = "primary"
                aria-label      = "icon label tabs example"
            >
                <Tab icon={<SettingsIcon />} label="Settings" />
                <Tab icon={<ResultsIcon />} label="Details" />
            </Tabs>
        </Paper>
    );
}
