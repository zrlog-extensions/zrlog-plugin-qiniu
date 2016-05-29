<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>${_plugin.name} - V${_plugin.version}</title>

    <link rel="stylesheet" href="assets/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="assets/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="assets/css/ace.min.css"/>
    <link rel="stylesheet" href="assets/css/ace-rtl.min.css"/>
    <link rel="stylesheet" href="assets/css/ace-skins.min.css"/>
    <link rel="stylesheet" href="assets/css/jquery.gritter.css"/>

    <script src="assets/js/jquery-2.0.3.min.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>
    <script src="assets/js/typeahead-bs2.min.js"></script>
    <script src="assets/js/ace-elements.min.js"></script>
    <script src="assets/js/ace.min.js"></script>
    <script src="assets/js/ace-extra.min.js"></script>

    <script src="assets/js/jquery.gritter.min.js"></script>
    <script src="assets/js/bootbox.min.js"></script>
    <script src="js/set_update.js"></script>
</head>
<body>
<div class="main-container">
    <div class="col-xs-12">
    <div class="page-header">
        <h1>
            云存储设置
            <small>
                <i class="icon-double-angle-right"></i>
                信息设置
            </small>
            <p class="text-right">
                <a href="http://blog.zrlog.com/post/qiniu-install">如何寻找这些信息？</a>
            </p>
        </h1>
    </div>
    <!-- /.page-header -->
        <input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5"/>
        <form class="form-horizontal" id="ajaxyunstore" role="form">
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> AK </label>

                <div class="col-sm-9">
                    <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                           value="${access_key}" name="access_key">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> SK </label>

                <div class="col-sm-9">
                    <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                           value="${secret_key}" name="secret_key">

                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 域名 </label>

                <div class="col-sm-9">
                    <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5"
                           value="${host}" name="host">

                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 仓库名 </label>

                <div class="col-sm-9">
                    <input type="text" id="form-field-1" placeholder="" class="col-xs-10 col-sm-5" value="${bucket}"
                           name="bucket">

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
    </div>
</div>
</body>
</html>