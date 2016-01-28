/**
 * Created by MartinTiefengrabner on 20/08/15.
 */


var API_URL = "YOUR URL HERE";

var contentWrapper;

var signs = [];

var showCardsImmediately = false;



$(document).ready(function () {

    contentWrapper = $("#content");

    $("#close-lighbox").click(function(e){
        e.preventDefault();
       hideLightbox();
    });

    $('#input-search-term').keypress(function(e){
        if(e.keyCode==13)
            $('#button-search').click();
    });

    $("#button-search").click(function (e) {
        showLoadingState();

        if($("#search-box").hasClass("search-box-center")){
            $("#search-box").switchClass("search-box-center", "search-box-top", 500)
            $("#logo").switchClass("logo-middle", "logo-top", 500)
            $("#input-search-term").switchClass("input-lg", "input-sm", 500)
            $("#button-search").switchClass("btn-lg", "btn-sm", 500)
            $("#bg").switchClass("bg-middle", "bg-top", 500)
            $("#bg-overlay").switchClass("bg-overlay-middle", "bg-overlay-top", 500)
            $("#header-box").switchClass("header-box-middle", "header-box-top", 500, function () {
                $("body").addClass("top");
                showSearchResultsAsCards(signs);
            });
        }
        else{
            showCardsImmediately = true;
        }

        searchString = $("#input-search-term").val();

        call = API_URL + "signs/?q=" + searchString;
        console.log(API_URL);
        $.get(call,
            "",
            function (data) {
                signs = data;

                if( showCardsImmediately){
                    showSearchResultsAsCards(signs);
                }
            }
        )
            .fail(function (error, tmp, err) {
                console.log(error.responseText);
                console.log(tmp);
                console.log(err);
            });
    });
})

function showLoadingState(){
    contentWrapper.html("");
}

function hideLoadingState(){

}

function showSearchResult(signs) {
    contentWrapper.html("");
    signs.forEach(function (sign, i) {
        line = [];
        line.push('<div class="panel panel-primary sign">')
        line.push('<div class="panel panel-body">')
        line.push("<h3>");
        line.push('<a href="/dictionary/sign/' + sign.id + '/">' + sign.name + '</a>');
        line.push("</h3>");
        line.push("<h4>" + sign.date + "</h4>");
        line.push('<div class="tags"></div>')
        if (sign.hasOwnProperty("tags")) {
            sign.tags.forEach(function (tag, i) {
                line.push('<span class="label label-primary">' + tag.tag + '</span>');
            });
        }

        line.push("</div>");
        line.push("</div>");
        contentWrapper.append(line.join("\n"));
    });
}

//function showSearchResultsAsCards(signs) {
//    contentWrapper.html("");
//    signs.forEach(function (sign, i) {
//        line = [];
//        line.push('<div class="col-sm-4 bootcards-cards">');
//        line.push('<div class="panel panel-default panel-ssc">');
//        line.push('<div class="panel-heading clearfix">');
//        line.push('<h3 class="panel-title pull-left">'+sign.name+'</h3>');
//        line.push('</div>');
//
//        line.push('<div class="panel-body">');
//
//        if (sign.hasOwnProperty("tags")) {
//            sign.tags.forEach(function (tag, i) {
//                line.push('<span class="label label-primary">' + tag.tag + '</span>');
//            });
//        }
//
//        line.push('</div>');
//
//        line.push('<div class="panel-footer">');
//        line.push('<small>Built with Bootcards - Base Card</small>');
//        line.push('</div>');
//        line.push('</div>');
//        line.push('</div>');
//        contentWrapper.append(line.join("\n"));
//    });
//}

function showSearchResultsAsCards(signs) {

    hideLoadingState();

    signs.forEach(function (sign, i) {

        signCard = $(createSignCard(sign, i).join("")).hide();
        contentWrapper.append(signCard);



    });
    var timeOut = 0;
    //[].reverse.call($(contentWrapper).children()).each(function(i,el){
   $(contentWrapper).children().each(function(i,el){

        setTimeout(function(){
            $(el).show('fast');
        },timeOut);

       timeOut += Math.max(0,(300-(Math.pow(i,2)*10)));
       console.log(timeOut);
   });

    $(".bootcards-cards-link").click(function(event){
        event.preventDefault();
        index = $(this).data("idx");
        playSign(signs[index]);
    });
}

function createSignCard(sign, i){
    line = [];

    line.push('<div class="col-sm-4 bootcards-cards" display="none">');
    line.push('<a class="bootcards-cards-link" href="#" data-idx="'+i+'">');
    line.push('<div class="panel panel-default panel-ssc">');

    line.push('<div class="panel-heading clearfix">');
    line.push('<h2 class="panel-title pull-left">'+sign.name+'</h2>');
    line.push('</div>');


    line.push('<div class="panel-body">');

    if (sign.hasOwnProperty("tags")) {
        sign.tags.forEach(function (tag, i) {
            line.push('<span class="label label-primary label-ssc">' + tag.tag + '</span>');
        });
    }

    line.push('</div>');

    line.push('<div class="panel-footer">');
    line.push('<small class="left">'+sign.date+'</small>');
    line.push('</div>');
    line.push('</div>');
    line.push('</a>');
    line.push('</div>');

    return line;
}

function playSign(sign){
    $("#sign-title").html(sign.name);
    showLightBox();
    setTimeout(function(){
        sendSignDataToUnitPlayer(sign.sign)
    }, 1000);
}


function showSign() {
    $("#panel-sign-heading").html("<h2>" + sign.name + "</h2>");

    content = [];

    content.push("<h4>" + sign.date + "</h4>")
    sign.tags.forEach(function (tag, i) {
        content.push('<span class="label label-primary">' + tag.tag + '</span>');
    });
    contentWrapper.html(content.join("\n"));
}

function hideLightbox(){
    $("#button-search").prop('disabled', false);
    $("#input-search-term").prop('disabled', false);
    $("#unity-lightbox").css("z-index",0);
}

function showLightBox(){
    $("#button-search").prop('disabled', true);
    $("#input-search-term").prop('disabled', true);
    $("#unity-lightbox").css("z-index","100");
}

function sendSignDataToUnitPlayer(sign){
    u.getUnity().SendMessage("App","StartAsPlayer",sign);
}

function onUnitySceneLoaded(){
    console.log("ready");
    //sendSignDataToUnitPlayer();

}