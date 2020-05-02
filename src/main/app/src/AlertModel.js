import React, {useContext, useEffect, useState} from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import MuiAlert from '@material-ui/lab/Alert';
import {AlertContext} from "./AlertContext";

function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export function SnackbarModel() {
    const model = useContext(AlertContext);
    return (
        <Snackbar
            open    ={model.open}
            autoHideDuration={1000}
            onClose ={model.onClose}
            anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        >
            <Alert onClose={model.onClose} severity={model.severity}>
                {model.message}
            </Alert>
        </Snackbar>
    )
}

export function useAlertModel() {
    const [open, setOpen]               = useState(false);
    const [severity, setSeverity]       = useState("");
    const [message, setMessage]         = useState("");

    function handleOpen () {
        setOpen(true);
    }

    function handleClose ()  {
        setOpen(false);
    }

    function send(s, m) {
        setSeverity(s);
        setMessage(m);
        setOpen(true);
    }

    useEffect(() => {
        setTimeout(() => {
            handleClose();
        }, 6000)
    }, [message]);

    return {
        open,
        handleOpen,
        handleClose,
        severity,
        message,
        setSeverity,
        setMessage,
        send
    }
}
