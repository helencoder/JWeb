/**
 * Created by zhenghailun on 2018/3/20.
 */
/**
 * Created by 80231428 on 2018/3/19.
 */
function goExtract(){
    var input = $("#content").val();
    if (input.length < 100) {
        alert("大哥，别闹，你不觉得你的文本有点短了吗？")
    } else {
        $.ajax({
            url: '/extract',
            type: 'POST',
            contentType : "application/json",
            dataType : 'json',
            data: JSON.stringify({
                content:input,
                source:"web"
            }),
            success: function(data) {
                goResult(data);
            },
            error: function(data) {
                console.log(data);
            }
        });
    }
}

function goResult(data){
    var obj = data;

    if(obj.code == "100000"){
        // 修改提交按钮
        $("#submit").css("opacity", "0.3");
        // 显示结果
        $(".label-des").css("color", "black")
        $(".label-des").text("");
        $(".label-des").text("样本关键词如下：");

        $("#feedback").text("");
        $("#feedback").append(data.msg);
        $("#feedback").css("display", "inline");
    } else {
        $("#feedback").css("display", "inline");
        console.log(obj.msg);
    }
}

function catchInputArea(){
    var content = document.getElementById("content");
    content.addEventListener("input", change, true);
    function change(){
        if(content.value){
            $("#submit").css("opacity", "1");
        }else{
            $("#submit").css("opacity", "0.3");
        }
        $("#feedback").css("display", "none");
    }
}