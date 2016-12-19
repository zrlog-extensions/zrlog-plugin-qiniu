<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>${_plugin.name} - V${_plugin.version}</title>

    <link rel="stylesheet" href="assets/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="assets/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="assets/css/bootstrap-switch.css"/>
    <link rel="stylesheet" href="assets/css/jquery.gritter.css"/>

    <script src="assets/js/jquery-2.0.3.min.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>
    <script src="assets/js/bootstrap-switch.js"></script>
    <script src="assets/js/jquery.gritter.min.js"></script>
    <script src="js/set_update.js"></script>
    <script>
        $(function(){
            $("[name='syncTemplate']").bootstrapSwitch();
        })
    </script>

</head>
<body>
<div class="page-header">
    <h3>
        七牛云存储设置
        <a href="http://blog.zrlog.com/post/qiniu-install" style="float:right">如何寻找这些信息？</a>
    </h3>
</div>

<div class="rows">
    <div class="col-sm-12">
        <form class="form-horizontal" id="ajaxyunstore" checkbox="syncTemplate" role="form">
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> AccessKey </label>

                <div class="col-sm-6">
                    <input type="text" placeholder="" class="form-control"
                           value="${access_key!''}" name="access_key">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> SecretKey </label>

                <div class="col-sm-6">
                    <input type="text" placeholder="" class="form-control"
                           value="${secret_key!''}" name="secret_key">

                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> 域名 </label>

                <div class="col-sm-4">
                    <input type="text" placeholder="" class="form-control"
                           value="${host!''}" name="host">

                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> 仓库名 </label>

                <div class="col-sm-3">
                    <input type="text" placeholder="" class="form-control"
                           value="${bucket!''}"
                           name="bucket">

                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> 主题静态文件同步 </label>

                <div class="col-sm-9">
                    <input type="hidden" id="syncTemplate" value="off">
                    <input name="syncTemplate" id="syncTemplate-switch" type="checkbox"
                    <#if syncTemplate?? && syncTemplate =='on'>checked="checked"</#if>
                    >
                </div>
            </div>
            <hr/>

            <div class="clearfix form-actions">
                <div class="col-md-offset-3 col-md-9">
                    <button class="btn btn-info" type="button" id="yunstore">
                        <i class="icon-ok bigger-110"></i>
                        提交
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
<input id="gritter-light" checked="" type="checkbox" style="display:none"/>
</body>
</html>