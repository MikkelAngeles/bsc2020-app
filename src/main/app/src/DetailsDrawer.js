import React, {useContext, useState} from 'react';
import {makeStyles} from "@material-ui/core/styles";
import Fab from '@material-ui/core/Fab';
import AddIcon from '@material-ui/icons/Add';
import SearchIcon from '@material-ui/icons/Search';
import ResultsIcon from '@material-ui/icons/List';
import ChartIcon from '@material-ui/icons/BarChart';
import PropertyIcon from '@material-ui/icons/FolderOpen';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowRightIcon from '@material-ui/icons/ArrowRight';
import FolderIcon from '@material-ui/icons/Folder';
import Drawer from "@material-ui/core/Drawer";
import VirtualList from "./VirtualList";
import Button from "@material-ui/core/Button";
import TabBar from "./TabBar";
import HistoryList from "./HistoryList";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import HistoryChart from "./HistoryChart";
import {graphTitle} from "./selectionProperties";
import {MapContext} from "./MapContext";
import TreeItem from "@material-ui/lab/TreeItem";
import Typography from "@material-ui/core/Typography";
import TreeView from "@material-ui/lab/TreeView";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import Input from "@material-ui/core/Input";
import TextField from "@material-ui/core/TextField";
import {SelectionContext} from "./SelectionContext";
import Chip from "@material-ui/core/Chip";
import {criterionTemplate} from "./SelectionModel";

const useTreeItemStyles = makeStyles((theme) => ({
    root: {
        color: theme.palette.text.secondary,
        '&:hover > $content': {
            backgroundColor: theme.palette.action.hover,
        },
        '&:focus > $content, &$selected > $content': {
            backgroundColor: `var(--tree-view-bg-color, ${theme.palette.grey[400]})`,
            color: 'var(--tree-view-color)',
        },
        '&:focus > $content $label, &:hover > $content $label, &$selected > $content $label': {
            backgroundColor: 'transparent',
        },
    },
    content: {
        color: theme.palette.text.secondary,
        borderTopRightRadius: theme.spacing(2),
        borderBottomRightRadius: theme.spacing(2),
        paddingRight: theme.spacing(1),
        fontWeight: theme.typography.fontWeightMedium,
        '$expanded > &': {
            fontWeight: theme.typography.fontWeightRegular,
        },
    },
    group: {
        marginLeft: 0,
        '& $content': {
            paddingLeft: theme.spacing(2),
        },
    },
    expanded: {},
    selected: {},
    label: {
        fontWeight: 'inherit',
        color: 'inherit',
    },
    labelRoot: {
        display: 'flex',
        alignItems: 'center',
        padding: theme.spacing(0.5, 0),
    },
    labelIcon: {
        marginRight: theme.spacing(1),
    },
    labelText: {
        fontWeight: 'inherit',
        flexGrow: 1,
    },
}));

function StyledTreeItem(props) {
    const classes = useTreeItemStyles();
    const { labelText, labelIcon: LabelIcon, labelInfo, color, bgColor, ...other } = props;

    return (
        <TreeItem
            label={
                <div className={classes.labelRoot}>
                    <LabelIcon color="inherit" className={classes.labelIcon} />
                    <Typography variant="body2" className={classes.labelText}>
                        {labelText}
                    </Typography>
                    <Typography variant="caption" color="inherit">
                        {labelInfo}
                    </Typography>
                </div>
            }
            style={{
                '--tree-view-color': color,
                '--tree-view-bg-color': bgColor,
            }}
            classes={{
                root: classes.root,
                content: classes.content,
                expanded: classes.expanded,
                selected: classes.selected,
                group: classes.group,
                label: classes.label,
            }}
            {...other}
        />
    );
}

const useStyles = makeStyles((theme) => ({
    root: {
        background: 'transparent'
    },
    drawer: {

    },
    contentRoot: {
        height: '100%'
    },
    toggleBtn: {
        position: 'absolute',
        right: 0,
        padding: '50px'
    },
    fab: {

    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 170,
    },
    chip: {
        margin: theme.spacing(0.5),
    },
}));

function PropertyList(props) {
    const classes = useStyles();
    const model = useContext(MapContext);
    const selectModel = useContext(SelectionContext);
    const [open, setOpen] = useState(false);
    const [selected, setSelected] = useState({key: "", value: ""});
    const [weightType, setWeightType] = useState('DISTANCE');
    const [weight, setWeight] = useState(1.0);
    const displayError = Number.isNaN(parseFloat(weight));

    function handleOpen(propKey, propValue) {
        setOpen(true);
        setSelected({key: propKey, value: propValue})
    }

    function handleClose() {
        setOpen(false)
    }

    function handleSetWeightType(evt) {
        setWeightType(evt.target.value)
    }

    function handleChangeWeight(evt) {
        setWeight(evt.currentTarget.value)
    }

    function handleSubmit() {
        if(displayError) return;
        let criterion = {weightType:weightType, property: {key:  selected.key, value: selected.value}, weightFactor:weight};
        selectModel.addCriterion(criterion);
        handleClose();
    }

    function getPropertiesList() {
        let rs = [];
        const buildItem = (propKey, propValue, i) => {
            return (
                <StyledTreeItem
                    nodeId    = {i}
                    labelText = {propValue}
                    labelIcon = {PropertyIcon}
                    color     = "#1a73e8"
                    bgColor   = "#e8f0fe"
                    onClick   = {() => handleOpen(propKey, propValue)}
                />
            )
        };

        let i = 2;
        for (let key of Object.keys(model.properties)) {
            let curr = model.properties[key];
            rs.push(
                <StyledTreeItem
                    nodeId      = {i++}
                    labelText   = {key}
                    labelIcon   = {FolderIcon}
                    labelInfo   = {curr.length}
                >
                    {curr.map((v, k) => buildItem(key, v, (i * k)))}
                </StyledTreeItem>
            )
        }
        return rs;
    }

    return (
        <>
            <Dialog onClose={handleClose} aria-labelledby="customized-dialog-title" open={open}>
                <DialogTitle id="customized-dialog-title" onClose={handleClose}>
                    Set criteria for property
                    <Typography variant="subtitle2" style={{textAlign: 'center'}}>
                        Key: <strong>{selected.key}</strong> value: <strong>{selected.value}</strong>
                    </Typography>
                </DialogTitle>
                <DialogContent dividers>
                    <FormControl className={classes.formControl}>
                        <InputLabel id="demo-dialog-select-label">Edge Weight Type</InputLabel>
                        <Select
                            labelId     = "demo-dialog-select-label"
                            id          = "demo-dialog-select"
                            value       = {weightType}
                            onChange    = {handleSetWeightType}
                            input       = {<Input />}
                        >
                            <MenuItem value="" disabled>
                                <em>Edge weight type</em>
                            </MenuItem>
                            <MenuItem value={'DISTANCE'}>Distance</MenuItem>
                            <MenuItem value={'TIME'}>Time</MenuItem>
                        </Select>
                        <div style={{marginTop: '20px'}}>
                            <TextField
                                error       = {displayError}
                                id          = "filled-error-helper-text"
                                label       = "Edge weight"
                                value       = {weight}
                                helperText  = {"Value must parse to a number."}
                                onChange    = {handleChangeWeight}
                            />
                        </div>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={handleSubmit} color="primary">
                        Submit
                    </Button>
                </DialogActions>
            </Dialog>

            <div style={{textAlign: 'center', borderBottom: '1px solid #dedede', paddingBottom: '10px'}}>
                {selectModel.criteria.map((data) =>
                    <Chip
                        label       = {data.property.key + " (" + data.property.value + ") " + data.weightType + " x " + data.weightFactor}
                        onDelete    = {() => selectModel.removeCriterion(data.property.key, data.property.value)}
                        className   = {classes.chip}
                        key         = {data.property.key}
                        onClick     = {() => handleOpen(data.property.key , data.property.key )}
                    />
                )}
            </div>

            <TreeView
                className               = {classes.root}
                defaultExpanded         = {['3']}
                defaultCollapseIcon     = {<ArrowDropDownIcon />}
                defaultExpandIcon       = {<ArrowRightIcon />}
                defaultEndIcon          = {<div style={{ width: 24 }} />}
            >
                {getPropertiesList()}
            </TreeView>
        </>
    );
}

export default function DetailsDrawer(props) {
    const classes = useStyles();
    const model = useContext(MapContext);
    const selectModel = useContext(SelectionContext);
    const [open, setOpen] = useState(false);
    const [openChart, setOpenChart] = useState(false);
    const [openCriteria, setOpenCriteria] = useState(false);
    const [tab, setTab] = useState(0);

    function handleOpenVertices() {
        setOpen(true);
        setTab(0);
    }

    function handleClose() {
        setOpen(false)
    }

    function handleOpenChart() {
        setOpenCriteria(false);
        setOpenChart(true);
    }

    function handleCloseChart() {
        setOpenChart(false)
    }

    function handleOpenHistory() {
        setOpen(true);
        setTab(1);
    }

    function handleOpenCriteria() {
        setOpenChart(false);
        setOpenCriteria(true);
    }

    function handleCloseCriteria() {
        setOpenCriteria(false)
    }

    return (
        <div className={classes.root}>
            <div className={classes.toggleBtn}>
                <Fab className={classes.fab} color="primary" onClick={handleOpenCriteria}>
                    <AddIcon />
                </Fab>

                <Fab className={classes.fab} color="primary" onClick={handleOpenVertices}>
                    <SearchIcon />
                </Fab>

                <Fab className={classes.fab} color="primary" onClick={handleOpenHistory}>
                    <ResultsIcon />
                </Fab>

                <Fab className={classes.fab} color="primary" onClick={handleOpenChart}>
                    <ChartIcon />
                </Fab>
            </div>
            <Drawer className={classes.drawer} open={open}  onClose={handleClose} anchor='right' variant="temporary">
                <div className={classes.contentRoot}>
                    <TabBar tab={tab} onChange={setTab} tabs={[
                        {
                            label: 'Vertices',
                            icon: <SearchIcon />
                        },
                        {
                            label: 'History',
                            icon: <ResultsIcon />
                        }
                    ]}/>

                    <div style={{marginTop: '2px'}}>
                        {tab === 0 ? <VirtualList /> : null}
                        {tab === 1 ? <HistoryList /> : null}
                    </div>
                </div>
                <Button variant="outlined" color="secondary" onClick={handleClose}>Close</Button>
            </Drawer>

            <Dialog onClose={handleCloseChart} aria-labelledby="customized-dialog-title" open={openChart}>
                <DialogTitle id="customized-dialog-title" onClose={handleCloseChart}>
                    Data chart for {graphTitle(model.selectedGraph)}
                </DialogTitle>
                <DialogContent dividers>
                    <HistoryChart />
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={handleCloseChart} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>


            <Dialog onClose={handleCloseCriteria} aria-labelledby="customized-dialog-title" open={openCriteria}>
                <DialogTitle id="customized-dialog-title" onClose={handleCloseCriteria}>
                    List of all available properties
                </DialogTitle>
                <DialogContent dividers>
                    <PropertyList />
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={handleCloseCriteria} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>

        </div>
    )
}