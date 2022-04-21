$(document).ready(function() {
    $(".nn-like").on("click", function() {
        var id = $(this).closest("[data-id]").attr("data-id");
        var url = "/product/like/" + id;
        fetch(url).then(resp => resp.json()).then(json => {
            alert("Đã tăng số lượt like: " + json)
        });
    });

    $(".nn-share").on("click", function() {
        id = $(this).closest("[data-id]").attr("data-id");
        $("#share-dialog").modal('show');
    });

    $(".nn-share-send").on("click", function() {
        var url = "/product/share-send";
        var form = {
            sender: $("#sender").val(),
            receiver: $("#receiver").val(),
            subject: $("#subject").val(),
            text: $("#text").val(),
            product: { id: id }
        }
        const options = {
            method: 'POST',
            body: JSON.stringify(form),
            headers: {
                'Content-Type': 'application/json'
            }
        }
        fetch(url, options).then(resp => {
            $("#share-dialog").modal('hide')
        });
    })
});