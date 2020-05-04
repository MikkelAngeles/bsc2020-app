import React, {useContext, useEffect, useState} from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import MuiAlert from '@material-ui/lab/Alert';
import {AlertContext} from "./AlertContext";
import IconButton from "@material-ui/core/IconButton";
import {makeStyles} from "@material-ui/styles";
import CloseIcon from '@material-ui/icons/Close';

const useStyles = makeStyles((theme) => ({
    close: {
        padding: 0,
    },
}));

function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export function SnackbarModel() {
    const model = useContext(AlertContext);
    const classes = useStyles();

    return (
        <Snackbar
            open                = {model.open}
            onClose             = {model.onClose}
            anchorOrigin        = {{ vertical: 'top', horizontal: 'center' }}
            autoHideDuration    = {1000}
        >
            <Alert onClose={model.onClose} severity={model.severity} action = {
                <React.Fragment>
                    <IconButton
                        aria-label  = "close"
                        color       = "inherit"
                        className   = {classes.close}
                        onClick     = {model.handleClose}
                    >
                        <CloseIcon />
                    </IconButton>
                </React.Fragment>
            }>
                {model.message}
            </Alert>
        </Snackbar>
    )
}
let timeout;
export function useAlertModel() {
    const [open, setOpen]               = useState(false);
    const [severity, setSeverity]       = useState("");
    const [message, setMessage]         = useState("");
    const [timer, setTimer]             = useState(6000);

    function handleOpen () {
        setOpen(true);
    }

    function handleClose ()  {
        clearTimeout(timeout);
        setOpen(false);
    }

    function error(m) {
        send("error", m);
    }

    function warning(m) {
        send("warning", m);
    }

    function info(m) {
        send("info", m);
    }

    function success(m) {
        send("success", m);
    }

    function send(s, m) {
        setSeverity(s);
        setMessage(m);
        setOpen(true);
    }

    useEffect(() => {
        function handleTimer() {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                handleClose();
            }, timer)
        }
        handleTimer();
    }, [message]);

    return {
        open,
        handleOpen,
        handleClose,
        severity,
        message,
        setSeverity,
        setMessage,
        setTimer,
        send,
        error,
        warning,
        info,
        success
    }
}
