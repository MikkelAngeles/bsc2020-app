import React, {createContext} from "react";
import PropTypes from "prop-types";
import {useSelectionModel} from "./SelectionModel";

export const SelectionContext = createContext(null);

export default function SelectionProvider ({ children }) {
    const model = useSelectionModel();

    return (
        <SelectionContext.Provider value={model}>
            {children}
        </SelectionContext.Provider>
    )
}

SelectionProvider.propTypes = {
    children: PropTypes.oneOf([PropTypes.array, PropTypes.element]).isRequired
};