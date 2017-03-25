$(function () {
    //var name=$.session.get('username');alert(name);
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
        detailView: false,
        columns: [{
            checkbox: true
        }, {
            field: 'traceId',
            title: '交易号'
        }, {
            field: 'id',
            title: 'ID',
            visible: false
        }, {
            field: 'parentId',
            title: '父级',
            visible: false
        }, {
            field: 'childId',
            title: '子级',
            visible: false
        }, {
            field: 'name',
            title: '名称'
        }, {
            field: 'timestamp',
            title: '时间',
            formatter: function (value) {
                return $.myTime.UnixToDate(parseInt(value), true,8);
                //return value
            }
        }, {
            field: 'duration',
            title: '消耗时间(s)'
        }, {
            field: 'backupSize',
            title: '备份文件大小'
        }, {
            field: 'backupType',
            title: '备份文件类型',
            formatter: function (value) {
                if(value=='0'){
                    return '全量备份'
                }
                if(value=='1'){
                    return '增量备份'
                }

            }
        }, {
            field: 'backupDirectory',
            title: '备份路径'
        }]
    });
    $('#logout').click(function(){
        $.ajax({
            url: '/logout',
            type: 'post',
            success: function (result) {
                if (result) {
                    window.location.reload();
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
            alert('请至少选择一条数据');
            //commonAlert('请至少选择一条数据');
            return
        }
        //console.log(JSON.stringify(selectionsIds));
        //alert(selectionsIds);
        $.ajax({
            url: '/pluto/delete',
            type: 'post',
            data: {'ids': selectionsIds},
            success: function (result) {
                if (result) {
                    if (result.code=='0000') {
                        alert('删除成功');
                        $table.bootstrapTable('refresh')
                    } else {
                        alert(result.message)
                    }
                }
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
