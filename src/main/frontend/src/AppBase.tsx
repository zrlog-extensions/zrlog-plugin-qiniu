import {FunctionComponent} from "react";
import {StorageInfoResponse} from "./index";
import StorageSettings from "./components/StorageSettings";

export type AppBaseProps = {
    pluginInfo: StorageInfoResponse;
}

const AppBase: FunctionComponent<AppBaseProps> = ({pluginInfo}) => {
    return <StorageSettings data={pluginInfo}/>;
}

export default AppBase;
