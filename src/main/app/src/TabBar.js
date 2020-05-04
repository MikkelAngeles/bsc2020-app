import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import Tabs from '@material-ui/core/Tabs';
import Tab from "@material-ui/core/Tab";

const useStyles = makeStyles({
    root: {
        flexGrow: 1,
        borderTop: '1px solid #ccc'
    },
});

export default function TabBar(props) {
    const classes = useStyles();
    const {onChange, tab, tabs} = props;

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
                {tabs.map((v,k) =>
                    <Tab icon={v.icon} label={v.label} key={k} />
                )}
            </Tabs>
        </Paper>
    );
}
