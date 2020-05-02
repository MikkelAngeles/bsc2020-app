import React, {createContext} from "react";
import PropTypes from "prop-types";
import {useMapModel} from "./MapModel";

export const MapContext = createContext(null);

export default function MapProvider ({ children }) {
    const model = useMapModel();

    return (
        <MapContext.Provider value={model}>
            {children}
        </MapContext.Provider>
    )
}

MapProvider.propTypes = {
    children: PropTypes.oneOf([PropTypes.array, PropTypes.element]).isRequired
};