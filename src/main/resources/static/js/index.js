$(function(){
    var $table =$('#table');
    $(window).resize(function() {
        $table.bootstrapTable('resetView', {
            height: tableHeight()
        })
    });
    $table.bootstrapTable({
        //url: 'js/1.json',
        url: '/pluto/backups',
        method: 'get',
        responseHandler:function(res) {
            return res.content;
        },
        toolbar: '#toolbar',
        striped: true,
        cache: false,
        pagination: true,
        sortable: false,
        sortName:"timestamp",
        queryParams: oTableInit.queryParams,
        sidePagination: "client",
        pageNumber:1,
        pageSize: 25,
        pageList: [ 25, 50, 100, 200],
        search: false,
        strictSearch: true,
        showColumns: true,
        showRefresh: true,
        minimumCountColumns: 2,
        clickToSelect: true,
        height: tableHeight(),
        uniqueId: "id",
        showToggle:false,
        cardView: false,
        detailView: false,
        columns: [{
            checkbox:true
        },{
            field: 'traceId',
            title: '交易号',
            visible:false
        }, {
            field: 'id',
            title: 'ID'
        }, {
            field: 'parentId',
            title: '父级',
            visible: false
        }, {
            field: 'childId',
            title: '子级',
            visible: false
        },{
            field: 'name',
            title: '名字'
        },{
            field: 'timestamp',
            title: '时间',
            formatter:function(value){
                return $.myTime.UnixToDate(value,true);
                //return value
            }
        },{
            field: 'duration',
            title: '消耗时间(s)'
        },{
            field: 'backupSize',
            title: '备份文件大小'
        },{
            field: 'backupType',
            title: '备份文件类型'
        },{
            field: 'backupDirectory',
            title: '备份路径'
        } ]
    });
    $('#deleteData').click(function(){
        var selections=$table.bootstrapTable('getAllSelections');
        var selectionsIds=[];
        $.each(selections,function(index,value){
            selectionsIds.push(value.id)
        });
        if(!selectionsIds.length){
            alert('请至少选择一条数据');
            //commonAlert('请至少选择一条数据');
            return
        }
            //console.log(JSON.stringify(selectionsIds));
            //alert(selectionsIds);
        $.ajax({
            url: '/pluto/delete',
            type: 'post',
            data: {'ids':selectionsIds},
            success: function (result) {
                if (result) {
                    if (result.success) {
                        alert('删除成功');
                        $table.bootstrapTable('refresh')
                    } else {
                        alert('删除失败')
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
            UnixToDate: function(unixTime, isFull, timeZone) {
                if (typeof (timeZone) == 'number')
                {
                    unixTime = parseInt(unixTime) + parseInt(timeZone) * 60 * 60;
                }
                var time = new Date(unixTime);
                var ymdhis = "";
                ymdhis += time.getUTCFullYear() + "-";
                ymdhis += (time.getUTCMonth()+1) + "-";
                ymdhis += time.getUTCDate();
                if (isFull === true)
                {
                    ymdhis += " " + time.getUTCHours() + ":";
                    ymdhis += time.getUTCMinutes() + ":";
                    ymdhis += time.getUTCSeconds();
                }
                return ymdhis;
            }
        }
    });
});
