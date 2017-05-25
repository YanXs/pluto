$(function () {
   $('#username').text(sessionStorage.username);
    var $table = $('#table');
    $(window).resize(function () {
        $table.bootstrapTable('resetView', {
            height: tableHeight()
        })
    });
    $table.bootstrapTable({
        //url: 'js/1.json',
        url: '/pluto/backups',
        method: 'get',
        responseHandler: function (res) {
            return res.content;
        },
        toolbar: '#toolbar',
        striped: true,
        cache: false,
        pagination: true,
        sortable: false,
        sortName: 'timestamp',
        //queryParams: oTableInit.queryParams,
        sidePagination: "client",
        pageNumber: 1,
        pageSize: 25,
        pageList: [25, 50, 100, 200],
        search: false,
        strictSearch: true,
        showColumns: true,
        showRefresh: true,
        minimumCountColumns: 2,
        clickToSelect: true,
        height: tableHeight(),
        uniqueId: 'id',
        showToggle: false,
        cardView: false,
        detailView: true,
        columns: [{
            checkbox: true
        }, {
            field: 'traceId',
            title: '交易号',
            visible: false
        }, {
            field: 'id',
            title: 'ID',
            visible: false
        }, {
            field: 'name',
            title: '名称'
        }, {
            field: 'timestamp',
            title: '备份时间',
            formatter: function (value) {
                return $.myTime.UnixToDate(parseInt(value), true,8);
            }
        }, {
            field: 'duration',
            title: '备份用时(s)'
        }, {
            field: 'backupSize',
            title: '备份文件大小（MB）'
        }, {
            field: 'backupType',
            title: '备份类型',
            formatter: function (value) {
                if(value=='Partial'){
                    return '部分备份';
                }
                if(value=='Full'){
                    return '全量备份';
                }
                if(value=='Incremental'){
                    return '增量备份';
                }
            }
        }, {
            field: 'backupDirectory',
            title: '备份路径'
        }],
        onExpandRow:function(index,row,$detail){
            if(row.backupType=='Partial'){
                var detail=$detail.html('<p></p>').find('p');
                if(row.databases){
                    detail.html('数据库：'+row.databases.join(','));
                }else{
                    detail.html('没有数据库');
                }

            }else{
                $table.bootstrapTable('collapseRow',index);
            }

        }
    });
    $('#logout').click(function(){
        $.ajax({
            url: '/logout',
            type: 'post',
            success: function (result) {
                if (result.code=='0000') {
                    window.location.href='/'
                }
            }
        });
    });
    $('#deleteData').click(function () {
        var selections = $table.bootstrapTable('getAllSelections');
        var selectionsIds = [];
        $.each(selections, function (index, value) {
            selectionsIds.push(value.id)
        });
        if (!selectionsIds.length) {
            bootbox.alert({
                message:'请至少选择一条数据',
                size:'small'
            });
            return
        }
        bootbox.confirm({
            message: "确认删除吗",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> 取消'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> 确认'
                }
            },
            callback: function (result) {
                if(result){
                    $.ajax({
                        url: '/pluto/delete',
                        type: 'post',
                        data: {'ids': selectionsIds},
                        success: function (result) {
                            if (result) {
                                if (result.code=='0000') {
                                    bootbox.alert({
                                        message:'删除成功',
                                        size:'small'
                                    });
                                    $table.bootstrapTable('refresh')
                                } else {
                                    bootbox.alert({
                                        message:result.message,
                                        size:'small'
                                    });
                                }
                            }
                        }
                    });
                }

            }
        });

    });
    $('#restoreData').click(function () {
        var selections = $table.bootstrapTable('getAllSelections');
        var selectionsIds = [];
        $.each(selections, function (index, value) {
            selectionsIds.push(value.id)
        });
        if (!selectionsIds.length) {
            bootbox.alert({
                message:'请至少选择一条数据',
                size:'small'
            });
            $btn.button('reset');
            return
        }
        if(selectionsIds.length>1){
            bootbox.alert({
                message:'只能选择一条数据',
                size:'small'
            });
            $btn.button('reset');
            return
        }
        bootbox.confirm({
            message: "确认恢复吗",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> 取消'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> 确认'
                }
            },
            callback: function (result) {
                if(result){
                    var $btn=$('#restoreData').button('loading');
                    var dialog = bootbox.dialog({
                        title: '恢复记录中...',
                        message: '<p><i class="fa fa-spin fa-spinner"></i> Loading...</p>'
                    });
                    $.ajax({
                        url: '/pluto/restore',
                        type: 'post',
                        data: {'id': selectionsIds[0]},
                        success: function (result) {
                            if (result) {
                                $btn.button('reset');
                                dialog.modal('hide');
                                if (result.code=='0000') {
                                    bootbox.alert({
                                        message:'恢复成功',
                                        size:'small'
                                    });
                                    $table.bootstrapTable('refresh')
                                } else {
                                    bootbox.alert({
                                        message:result.message,
                                        size:'small'
                                    });
                                }
                            }
                        },
                        error:function(){
                            $btn.button('reset');
                            dialog.modal('hide');
                        }
                    });
                }

            }
        });

    });
    $('#fullbackupData').click(function () {
        $.ajax({
            url: '/pluto/instances',
            type: 'get',
            success: function (data) {
                if (data.content) {
                    var option=[];
                    $.each(data.content, function (index, values) {
                        option[index]={};
                        option[index].value=values;
                        option[index].text=values;
                    });
                    bootbox.prompt({
                        title: "请输入名字及选择数据库",
                        inputType: 'select',
                        inputOptions: option,
                        callback: function (result) {
                            console.log(result);
                            if(result==null){
                                return;
                            }
                            if(result.text==''){
                                bootbox.alert({
                                    message:'请输入名字！！！！！！！！！！'
                                });
                                return;
                            }
                            if(!result.selection){
                                bootbox.alert({
                                    message:'请选择数据库！！！！！！！！！！'
                                });
                                return;
                            }
                            var dialog = bootbox.dialog({
                                title: '备份文件中...',
                                message: '<p><i class="fa fa-spin fa-spinner"></i> Backing up...</p>'
                            });
                            $.ajax({
                                url: '/pluto/full/backup',
                                type: 'post',
                                data: {'name': result.text, 'databases':result.selection},
                                success: function (data) {
                                    if (data) {
                                        dialog.modal('hide');
                                        if (data.code=='0000') {
                                            bootbox.alert({
                                                message:'备份成功',
                                                size:'small'
                                            });
                                            $table.bootstrapTable('refresh')
                                        } else {
                                            bootbox.alert({
                                                message:data.message,
                                                size:'small'
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            },
            error:function(data){
                alert('参数出错'+data);
            }
        });

    });
    $('#partialBackupData').click(function(){
        var data=[1,2,3];
        var option=[];
        $.each(data, function (index, values) {
            option[index]={};
            option[index].value=values;
            option[index].text=values;
        });
        bootbox.prompt({
            title: "请输入名字及选择数据库",
            inputType: 'select',
            inputOptions: option,
            callback: function (result) {
                console.log(result);
                if(result==null){
                    return;
                }
                if(result.text==''){
                            bootbox.alert({
                                message:'请输入名字！！！！！！！！！！'
                            });
                            return;
                        }
                if(!result.selection){
                    bootbox.alert({
                        message:'请选择数据库！！！！！！！！！！'
                    });
                    return;
                }
                var dialog = bootbox.dialog({
                    title: '备份文件中...',
                    message: '<p><i class="fa fa-spin fa-spinner"></i> Backing up...</p>'
                });
                $.ajax({
                    url: '/pluto/partial/backup',
                    type: 'post',
                    data: {'name': result.text, 'databases':result.checkbox},
                    success: function (data) {
                        if (data) {
                            dialog.modal('hide');
                            if (data.code=='0000') {
                                bootbox.alert({
                                    message:'备份成功',
                                    size:'small'
                                });
                                $table.bootstrapTable('refresh')
                            } else {
                                bootbox.alert({
                                    message:data.message,
                                    size:'small'
                                });
                            }
                        }
                    }
                });
            }
        });
    });
    function tableHeight() {
        return $(window).height() - 20;
    }

    $.extend({
        myTime: {
            UnixToDate: function (unixTime, isFull, timeZone) {
                if (typeof (timeZone) == 'number') {
                    unixTime = parseInt(unixTime) + parseInt(timeZone) * 60 * 60 * 1000;
                }
                var time = new Date(unixTime);
                //console.log(time);
                var ymdhis = "";
                ymdhis += time.getUTCFullYear() + "-";
                ymdhis += (time.getUTCMonth() + 1) + "-";
                ymdhis += time.getUTCDate();
                if (isFull === true) {
                    ymdhis += " " + time.getUTCHours() + ":";
                    ymdhis += time.getUTCMinutes() + ":";
                    ymdhis += time.getUTCSeconds();
                }
                return ymdhis;
            }
        }
    });
});
