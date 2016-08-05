<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>${_plugin.name} - V${_plugin.version}</title>

    <link rel="stylesheet" href="assets/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="assets/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="assets/css/jquery.gritter.css"/>

    <script src="assets/js/jquery-2.0.3.min.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>
    <script src="assets/js/jquery.gritter.min.js"></script>
    <script src="js/set_update.js"></script>

</head>
<body>
<div class="page-header">
    <h3>
        七牛云存储设置
        <a href="http://blog.zrlog.com/post/qiniu-install" style="float:right">如何寻找这些信息？</a>
    </h3>
</div>

<form class="form-horizontal" id="ajaxyunstore" checkbox="syncTemplate" role="form">
    <div class="form-group">
        <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> AK </label>

        <div class="col-sm-9">
            <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                   value="${access_key!''}" name="access_key">
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> SK </label>

        <div class="col-sm-9">
            <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                   value="${secret_key!''}" name="secret_key">

        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 域名 </label>

        <div class="col-sm-9">
            <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                   value="${host!''}" name="host">

        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 仓库名 </label>

        <div class="col-sm-9">
            <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                   value="${bucket!''}"
                   name="bucket">

        </div>
    </div>
    <div class="form-group">
        <label for="form-field-1"
               class="col-sm-3 control-label no-padding-right"> 主题静态文件同步 </label>

        <div class="col-sm-9">
            <label>
                <input type="hidden" id="syncTemplate" value="off">
                <input name="syncTemplate" class="ace ace-switch ace-switch-6" type="checkbox"
                <#if syncTemplate?? && syncTemplate =='on'>checked="checked"</#if>
                >
            </label>
        </div>
    </div>
    <div class="space-4"></div>

    <div class="clearfix form-actions">
        <div class="col-md-offset-3 col-md-9">
            <button class="btn btn-info" type="button" id="yunstore">
                <i class="icon-ok bigger-110"></i>
                提交
            </button>
        </div>
    </div>
</form>
<input id="gritter-light" checked="" type="checkbox" style="display:none"/>
</body>
</html>