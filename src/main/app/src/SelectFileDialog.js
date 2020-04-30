import React from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Avatar from '@material-ui/core/Avatar';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import PersonIcon from '@material-ui/icons/Person';
import AddIcon from '@material-ui/icons/Add';
import Typography from '@material-ui/core/Typography';
import { blue } from '@material-ui/core/colors';

const files = [
    {
        id: 0,
        file: 'Hillerød',
        type: 'GeoJSON - OSM',
        vertices: 29711,
        edges: 80454,
        bytes: 3.7
    },
    {
        id: 1,
        file: 'New York City',
        type: 'DIMACS',
        vertices: '264,346',
        edges: '733,846',
        bytes: (2 + 3.6 + 3.5)
    },
    {
        id: 2,
        file: 'Florida',
        type: 'DIMACS',
        vertices: '1,070,376',
        edges: '2,712,798',
        bytes: (14 + 14 + 8.6)
    },
    {
        id: 3,
        file: 'California & Nevada',
        type: 'DIMACS',
        vertices: '1,890,815',
        edges: '4,657,742',
        bytes: (26 + 26 + 16)
    },
    {
        id: 4,
        file: 'Eastern USA',
        type: 'DIMACS',
        vertices: '3,598,623',
        edges: '8,778,114',
        bytes: (49 + 50 + 32)
    },
    {
        id: 5,
        file: 'Western USA',
        type: 'DIMACS',
        vertices: '6,262,104',
        edges: '15,248,146',
        bytes: (86 + 88 + 57)
    },
    {
        id: 6,
        file: 'Full USA',
        type: 'DIMACS',
        vertices: '23,947,347',
        edges: '58,333,344',
        bytes: (335 + 342 + 218)
    }
];

const useStyles = makeStyles({
    avatar: {
        backgroundColor: blue[100],
        color: blue[600],
    },
});

function SimpleDialog(props) {
    const classes = useStyles();
    const { onClose, selectedValue, open } = props;

    const handleClose = () => {
        onClose(selectedValue);
    };

    const handleListItemClick = (value) => {
        onClose(value);
    };

    return (
        <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
            <DialogTitle id="simple-dialog-title">Select graph</DialogTitle>
            <List>
                {files.map((f,k) => (
                    <ListItem button onClick={() => handleListItemClick(f.id)} key={f.id}>
                        <ListItemAvatar>
                            <Avatar className={classes.avatar}>
                                <PersonIcon />
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={f.file + " (" + f.type + ")"} secondary={"Vertices: " + f.vertices + ", Edges: " + f.edges + ", Size: " + f.bytes + " mb"} />
                    </ListItem>
                ))}

            </List>
        </Dialog>
    );
}

SimpleDialog.propTypes = {
    onClose: PropTypes.func.isRequired,
    open: PropTypes.bool.isRequired,
    selectedValue: PropTypes.string.isRequired,
};

export default function SelectFileDialog(props) {
    const [open, setOpen] = React.useState(false);
    const {selected, onSelect} = props;

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (value) => {
        if(value === 6) {

        }
        setOpen(false);
        onSelect(value);
    };

    return (
        <div>
            <Button variant="outlined" color="primary" onClick={handleClickOpen}>
                Select graph
            </Button>
            <SimpleDialog selectedValue={selected} open={open} onClose={handleClose} />
        </div>
    );
}
