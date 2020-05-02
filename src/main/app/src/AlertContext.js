import React, {createContext} from "react";
import PropTypes from "prop-types";
import {useAlertModel} from "./AlertModel";

export const AlertContext = createContext(null);

export default function AlertProvider ({ children }) {
    const model = useAlertModel();

    return (
        <AlertContext.Provider value={model}>
            {children}
        </AlertContext.Provider>
    )
}

AlertProvider.propTypes = {
    children: PropTypes.oneOf([PropTypes.array, PropTypes.element]).isRequired
};