import React from 'react'
import Terminal from 'react-console-emulator'

const commands = {
    echo: {
        description: 'Echo a passed string.',
        usage: 'echo <string>',
        fn: function () {
            return `${Array.from(arguments).join(' ')}`
        }
    },
    algorithms: {
        description: 'List of available algorithms',
        usage: 'echo <string>',
        fn: function () {
            return `A*, Dikstras`
        }
    },

};

export default function ReactTerminal() {
    return (
        <div style={{width: '500px', display:'flex', textAlign: 'left'}}>
            <Terminal
                commands        = {commands}
                welcomeMessage  = {'Shortest path terminal by Mikkel Helmersen. Use $help to get started'}
                promptLabel     = {'mhel@bsc:~$'}

            />
        </div>
    )
}