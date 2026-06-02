import {
    Button,
    Col,
    Divider,
    Form,
    Grid,
    Input,
    Row,
    Space,
    Switch,
    Tag,
    Typography,
    message,
    theme,
} from "antd";
import {CloudSyncOutlined, QuestionCircleOutlined, SaveOutlined} from "@ant-design/icons";
import axios from "axios";
import {FunctionComponent, useEffect, useMemo, useState} from "react";
import {StandardResponse, StorageConfig, StorageInfoResponse} from "../index";

type StorageSettingsProps = {
    data: StorageInfoResponse;
}

type StorageFormValues = {
    access_key?: string;
    secret_key?: string;
    host?: string;
    bucket?: string;
    private_bucket?: string;
    appId?: string;
    region?: string;
    syncTemplate?: boolean;
    syncHtml?: boolean;
    supportHttps?: boolean;
}

const enabled = (value?: string) => value === "on" || value === "true" || value === "1";

const toFormValues = (config: StorageConfig): StorageFormValues => ({
    access_key: config.access_key || "",
    secret_key: config.secret_key || "",
    host: config.host || "",
    bucket: config.bucket || "",
    private_bucket: config.private_bucket || "",
    appId: config.appId || "",
    region: config.region || "",
    syncTemplate: enabled(config.syncTemplate),
    syncHtml: enabled(config.syncHtml),
    supportHttps: enabled(config.supportHttps),
});

const switchValue = (value?: boolean) => value ? "on" : "off";

const StorageSettings: FunctionComponent<StorageSettingsProps> = ({data}) => {
    const {token} = theme.useToken();
    const screens = Grid.useBreakpoint();
    const isPhone = Boolean(screens.xs && !screens.sm);
    const isCompact = !screens.md;
    const [form] = Form.useForm<StorageFormValues>();
    const [loading, setLoading] = useState(false);
    const [messageApi, contextHolder] = message.useMessage();
    const provider = data.provider;
    const version = data.config.version || data.plugin.version;

    useEffect(() => {
        form.setFieldsValue(toFormValues(data.config || {}));
    }, [data.config, form]);

    const shellStyle = useMemo(() => ({
        maxWidth: 980,
        margin: "0 auto",
        padding: isPhone ? 12 : isCompact ? 16 : 24,
        color: token.colorText,
        background: token.colorBgLayout,
        minHeight: "100vh",
        boxSizing: "border-box" as const,
    }), [isCompact, isPhone, token]);

    const panelStyle = useMemo(() => ({
        padding: isPhone ? 16 : 24,
        border: `1px solid ${token.colorBorderSecondary}`,
        borderRadius: 8,
        background: token.colorBgContainer,
    }), [isPhone, token]);

    const submit = async (values: StorageFormValues) => {
        setLoading(true);
        try {
            const params: Record<string, string> = {
                access_key: values.access_key || "",
                secret_key: values.secret_key || "",
                host: values.host || "",
                bucket: values.bucket || "",
                syncTemplate: switchValue(values.syncTemplate),
            };
            if (provider.privateBucket) {
                params.private_bucket = values.private_bucket || "";
            }
            if (provider.appId) {
                params.appId = values.appId || "";
            }
            if (provider.regionLabel) {
                params.region = values.region || "";
            }
            if (provider.syncHtml) {
                params.syncHtml = switchValue(values.syncHtml);
            }
            if (provider.supportHttps) {
                params.supportHttps = switchValue(values.supportHttps);
            }

            const {data: response} = await axios.post<StandardResponse<unknown>>("update", new URLSearchParams(params), {
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
            });
            if (!response.success) {
                throw new Error(response.message || "保存失败");
            }
            messageApi.success("已保存");
        } catch (e) {
            messageApi.error(e instanceof Error ? e.message : "保存失败");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={shellStyle}>
            {contextHolder}
            <Space direction="vertical" size={20} style={{width: "100%"}}>
                <div style={{display: "flex", justifyContent: "space-between", gap: 16, flexWrap: "wrap"}}>
                    <Space direction="vertical" size={4}>
                        <Space wrap style={{maxWidth: "100%"}}>
                            <Typography.Title level={3} style={{margin: 0, fontSize: isPhone ? 20 : undefined}}>{provider.title || data.plugin.name}</Typography.Title>
                            <Tag>v{version}</Tag>
                        </Space>
                        <Typography.Text type="secondary" style={{display: "block", maxWidth: "100%"}}>{data.plugin.desc}</Typography.Text>
                    </Space>
                    {provider.helpUrl ? (
                        <Button icon={<QuestionCircleOutlined/>} href={provider.helpUrl} target="_blank" style={isPhone ? {width: "100%"} : undefined}>
                            帮助文档
                        </Button>
                    ) : null}
                </div>

                <div style={panelStyle}>
                    <Form form={form} layout="vertical" onFinish={submit} requiredMark={false}>
                        <Row gutter={[isCompact ? 12 : 16, 0]}>
                            <Col xs={24} md={12}>
                                <Form.Item label="AccessKey" name="access_key">
                                    <Input autoComplete="off"/>
                                </Form.Item>
                            </Col>
                            <Col xs={24} md={12}>
                                <Form.Item label="SecretKey" name="secret_key">
                                    <Input.Password autoComplete="new-password"/>
                                </Form.Item>
                            </Col>
                            <Col xs={24} md={12}>
                                <Form.Item label="访问域名" name="host">
                                    <Input placeholder="例如：static.example.com"/>
                                </Form.Item>
                            </Col>
                            <Col xs={24} md={12}>
                                <Form.Item label="Bucket 名称" name="bucket">
                                    <Input/>
                                </Form.Item>
                            </Col>
                            {provider.privateBucket ? (
                                <Col xs={24} md={12}>
                                    <Form.Item label="私有 Bucket 名称" name="private_bucket">
                                        <Input placeholder="用于数据库备份等私有文件"/>
                                    </Form.Item>
                                </Col>
                            ) : null}
                            {provider.appId ? (
                                <Col xs={24} md={12}>
                                    <Form.Item label="AppId" name="appId">
                                        <Input/>
                                    </Form.Item>
                                </Col>
                            ) : null}
                            {provider.regionLabel ? (
                                <Col xs={24} md={12}>
                                    <Form.Item label={provider.regionLabel} name="region">
                                        <Input/>
                                    </Form.Item>
                                </Col>
                            ) : null}
                        </Row>

                        <Divider/>

                        <Space direction="vertical" size={16} style={{width: "100%"}}>
                            <Form.Item label="主题静态文件同步" name="syncTemplate" valuePropName="checked" style={{marginBottom: 0}}>
                                <Switch checkedChildren="同步" unCheckedChildren="关闭"/>
                            </Form.Item>
                            <Typography.Text type="secondary">
                                <CloudSyncOutlined/> 开启后会同步当前主题暴露的静态资源。
                            </Typography.Text>
                            {provider.syncHtml ? (
                                <Form.Item label="静态缓存 HTML 文件同步" name="syncHtml" valuePropName="checked" style={{marginBottom: 0}}>
                                    <Switch/>
                                </Form.Item>
                            ) : null}
                            {provider.supportHttps ? (
                                <Form.Item label="启用 HTTPS" name="supportHttps" valuePropName="checked" style={{marginBottom: 0}}>
                                    <Switch/>
                                </Form.Item>
                            ) : null}
                        </Space>

                        <Divider/>

                        <Button type="primary" htmlType="submit" icon={<SaveOutlined/>} loading={loading} style={isPhone ? {width: "100%"} : undefined}>
                            保存
                        </Button>
                    </Form>
                </div>
            </Space>
        </div>
    );
};

export default StorageSettings;
