<script type="text/javascript">
    <!--
    var unityObjectUrl = "/dictionary/unity/UnityObject2.js";
    if (document.location.protocol == 'https:')
        unityObjectUrl = unityObjectUrl.replace("http://", "https://ssl-");
    document.write('<script type="text\/javascript" src="' + unityObjectUrl + '"><\/script>');
    -->
</script>
<script type="text/javascript">
    <!--
    var config = {
        width: 920,
        height: 600,
        params: { enableDebugging:"1" }

    };
    var u = new UnityObject2(config);

    jQuery(function() {

        var $missingScreen = jQuery("#unityPlayer").find(".missing");
        var $brokenScreen = jQuery("#unityPlayer").find(".broken");
        $missingScreen.hide();
        $brokenScreen.hide();

        u.observeProgress(function (progress) {
            switch(progress.pluginStatus) {
                case "broken":
                    $brokenScreen.find("a").click(function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        u.installPlugin();
                        return false;
                    });
                    $brokenScreen.show();
                    break;
                case "missing":
                    $missingScreen.find("a").click(function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        u.installPlugin();
                        return false;
                    });
                    $missingScreen.show();
                    break;
                case "installed":
                    $missingScreen.remove();
                    break;
                case "first":
                    break;
            }
        });
        u.initPlugin(jQuery("#unityPlayer")[0], "/dictionary/unity/unity.unity3d");
    });
</script>

<link type="text/css" rel="stylesheet" href="/dictionary/unity/unity.css">



</head>
<body>
<div class="panel panel-primary" id="panel-main">
    <div class="panel-heading" id="panel-sign-heading">

    </div>
    <div class="panel-body" id="panel-body-results">
        <div id="sign-info">

        </div>
        <div id="unityPlayer">
            <div class="missing">
                <a href="http://unity3d.com/webplayer/" title="Unity Web Player. Install now!">
                    <img alt="Unity Web Player. Install now!"
                         src="/dictionary/unity/getunity.png" width="193" height="63"/>
                </a>
            </div>
        </div>

    </div>
</div>

<script type="application/javascript">
    var signID =
    <?php echo $_GET['sign'];?>

    var API_URL = "YOUR URL HERE";

    var contentWrapper;

    var sign;

    $(document).ready(function () {

        contentWrapper = $("#sign-info");

        call = API_URL + "signs/" + signID + "/"
        console.log(API_URL);
        $.get(call,
            "",
            function (data) {
                sign = data;
                showSign();
           }
        )
            .fail(function (error, tmp, err) {
                console.log(error.responseText);
                console.log(tmp);
                console.log(err);
            });
    })

    function showSign() {
        $("#panel-sign-heading").html("<h2>" + sign.name + "</h2>");

        content = [];

        content.push("<h4>" + sign.date + "</h4>")
        sign.tags.forEach(function (tag, i) {
            content.push('<span class="label label-primary">' + tag.tag + '</span>');
        });
        contentWrapper.html(content.join("\n"));

        sendSignDataToUnitPlayer();
    }

    function sendSignDataToUnitPlayer(){
        u.getUnity().SendMessage("App","StartAsPlayer",sign.sign);
    }

    function onUnitySceneLoaded(){
        console.log("ready");
        sendSignDataToUnitPlayer();

    }

</script>