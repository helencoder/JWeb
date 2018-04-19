/**
 * Created by 80231428 on 2018/3/19.
 */
function goAudit(){
    var input = $("#content").val();
    $.ajax({
        url: '/audit',
        type: 'POST',
        contentType : "application/json",
        dataType : 'json',
        data: JSON.stringify({
            content:input,
            source:"web"
        }),
        success: function(result) {
            goResult(result);
        },
        error: function(result) {
            console.log(result);
        }
    });
}

function goFeedback(){
    var input = $("#content").val();
    var feedback = $("#feedback").val();
    $.ajax({
        url: '/home',
        type: 'POST',
        data: {
            Content: input,
            Feedback: feedback
        },
        success: function(data) {
            alert("臣领旨");
        },
        error: function(data) {
            console.log(data);
        }
    });
}

function goResult(result){

    var details = result.msg.split("\t")
    if(result.code == "100000"){
        if(result.data == "0"){
            $("#pos").css("color", "green");
        }else if(result.data == "1"){
            $("#neg").css("color", "red");
        }else {
            $("#nor").css("color", "yellow");
        }
        $("#feedback").text("");
        $("#feedback").append(details[0]);
        $("#feedback").css("display", "inline");
        $("#ml-feedback").text("");
        $("#ml-feedback").append(details[1]);
        $("#ml-feedback").css("display", "inline");
        $("#dl-feedback").text("");
        $("#dl-feedback").append(details[2]);
        $("#dl-feedback").css("display", "inline");
    } else {
        $("#feedback").css("display", "inline");
        console.log(result.msg);
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
        $("#neg").css("color", "#AAA");
        $("#pos").css("color", "#AAA");
        $("#nor").css("color", "#AAA");
        $("#feedback").css("display", "none");
        $("#dl-feedback").css("display", "none");
        $("#ml-feedback").css("display", "none");

    }
}