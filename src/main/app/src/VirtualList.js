import React, {useContext, useEffect, useRef, useState} from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { FixedSizeList } from 'react-window';
import {MapContext} from "./MapContext";
import Avatar from "@material-ui/core/Avatar";
import Chip from "@material-ui/core/Chip";

import {SelectionContext} from "./SelectionContext";
import DialogTitle from "@material-ui/core/DialogTitle";
import List from "@material-ui/core/List";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import PersonIcon from "@material-ui/icons/Person";
import Dialog from "@material-ui/core/Dialog";

import FromIcon from "@material-ui/icons/Flag";
import ToIcon from "@material-ui/icons/FlagOutlined";
import LocateIcon from "@material-ui/icons/MyLocation";
import TextField from "@material-ui/core/TextField";
import {useDebounce} from "use-lodash-debounce/dist";
import Typography from "@material-ui/core/Typography";

const useStyles = makeStyles((theme) => ({
    root: {
        height: '100%',
        backgroundColor: theme.palette.background.paper,
        display: 'flex',
        flexDirection: 'column'
    },
    fixedList: {
        height: '100%'
    }
}));


export function ItemMenu(props) {
    const { open, item, onClose} = props;
    const model = useContext(SelectionContext);

    function handleSetSource() {
        model.setRouteFrom(item.point, item.index);
        onClose();
    }

    function handleSetTarget() {
        model.setRouteTo(item.point, item.index);
        onClose();
    }

    function handleLocate() {
        model.setNearest(item.point, item.index);
        onClose();
    }

return (
    <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open}>
        <DialogTitle id="simple-dialog-title" style={{textAlign: 'center'}}>
            Action for vertex
            <Chip variant="outlined" color="primary" size="small" label={item.index} style={{marginLeft: '10px'}}/>
        </DialogTitle>

        <div style={{display: 'flex', flexDirection: 'column', padding: '0px 10px 0px 10px'}}>
            <Chip variant="outlined" avatar={<Avatar>Lon</Avatar>} size="small" label={`${item.point[0]}`} style={{marginBottom: '10px'}} />
            <Chip variant="outlined" avatar={<Avatar>Lat</Avatar>} size="small" label={`${item.point[1]}`} />
        </div>
        <List>
                <ListItem button onClick={handleSetSource}>
                    <ListItemAvatar>
                        <Avatar style={{background: 'green'}}>
                            <FromIcon />
                        </Avatar>
                    </ListItemAvatar>
                    <ListItemText primary={"Set as route from"}/>
                </ListItem>

            <ListItem button onClick={handleSetTarget}>
                <ListItemAvatar>
                    <Avatar style={{background: 'red'}}>
                        <FromIcon />
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary={"Set as route to"}/>
            </ListItem>

            <ListItem button onClick={handleLocate}>
                <ListItemAvatar>
                    <Avatar style={{background: 'blue'}}>
                        <LocateIcon />
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary={"Locate"}/>
            </ListItem>
        </List>
    </Dialog>
);
}

export default function VirtualList(props) {
    const classes = useStyles();
    const [anchorEl, setAnchorEl] = useState(null);
    const [open, setOpen] = useState(null);
    const [list, setList] = useState([]);
    const [listHeight, setListHeight] = useState( window.innerHeight - 250);
    const [srcInput, setSrcInput] = useState("");
    const debouncedValue          = useDebounce(srcInput, 300);
    const [selected, setSelected] = useState({index:0, point: []});
    //const {items, count} = props;

    const model = useContext(MapContext);
    let data    = model.graph.vertices;
    let listRef = useRef();
   /* let height  = listRef && listRef.current ? listRef.current.offsetHeight : 400;
*/
    function handleCloseMenu() {
        setOpen(false);
        setAnchorEl(null);
    }

    function handleChange(evt) {
        setSrcInput(evt.currentTarget.value);
    }

    function handleSearch(val) {
        let rs = [];
        if(!data) return setList(data);
        rs = data.filter(x =>
            x[0].toString().includes(val.toString()) ||
            x[1].toString().includes(val.toString())
        ).map(x => x);
        setList(rs);
    }

    function renderRow(props) {
        const { index, style} = props;
        let item = list[index];

        function handleClick(event) {
            setOpen(true);
            setAnchorEl(event.currentTarget);
            console.log(event.currentTarget);
            setSelected({index:index, point: item})
        }

        return (
            <ListItem button style={style} key={index} onClick={handleClick}>
                <Chip variant="outlined" color="primary" size="small" label={index} style={{marginRight: '10px'}} />
                <div style={{display: 'flex'}}>
                    <Chip variant="outlined" avatar={<Avatar>Lon</Avatar>} size="small" label={`${item[0]}`} style={{marginRight: '10px'}}/>
                    <Chip variant="outlined" avatar={<Avatar>Lat</Avatar>} size="small" label={`${item[1]}`} />
                </div>
            </ListItem>
        );
    }

    useEffect(() => {
        setList(model.graph.vertices);
    }, [model.graph.vertices]);

    useEffect(() => {
        handleSearch(debouncedValue);
    }, [debouncedValue]);

    useEffect(() => {
        function handleResize() {
            setListHeight(listRef && listRef.current ? listRef.current.offsetHeight - 100 : window.innerHeight - 200);
        }
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className={classes.root}>
            <Typography variant="h5" gutterBottom>
                Vertices
            </Typography>
            <TextField
                 value      = {srcInput}
                 id         = "standard-basic"
                 label      = "Search longitude or latitude"
                 onChange   = {handleChange}
                 style      = {{width: '100%', margin: '0px 0px 10px 0px'}}
            />
            <Typography variant="subtitle1" gutterBottom>
                Displaying {list.length} results
            </Typography>
            <div className={classes.fixedList} ref={listRef}>
                <FixedSizeList height={listHeight} width={500} itemSize={46} itemCount={list.length}>
                    {renderRow}
                </FixedSizeList>
                <ItemMenu open={open} anchorEl={anchorEl} item={selected} onClose={handleCloseMenu} />
            </div>
        </div>
    );
}