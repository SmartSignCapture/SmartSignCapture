<script language="javascript" type="text/javascript" src="/dictionary/js/app.js"></script>
<script type='text/javascript' src='/dictionary/lib/bootcards/1.1.2/js/bootcards.min.js'></script>


<link type="text/css" rel="stylesheet" href="/dictionary/lib/bootcards/1.1.2/css/bootcards-desktop.min.css">

<link type="text/css" rel="stylesheet" href="/dictionary/css/home.css">

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
        width: 615,
        height: 400,
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
<div id="page-wrapper">
    <div id="white-out"></div>
    <div id="bg" class="bg-middle"></div>
    <div id="bg-overlay" class="bg-overlay-middle"></div>
    <div id="search-box" class="search-box-center">
        <div id="logo-box" class="logo-box-middle">
            <div id="logo" class="logo-middle"></div>
            <h1 id="header-box" class="header-box-middle">SmartSignCapture</h1>
        </div>
        <div class="input-group" class="search-field-middle">
            <input type="text" class="form-control input-lg" placeholder="Search for..." id="input-search-term">
                <span class="input-group-btn">
                    <button class="btn btn-lg" type="button" id="button-search">Go!</button>
                </span>
        </div>
        <!-- /input-group -->
    </div>
    <!-- /.col-lg-6 -->

<div id="content">
</div>
</div>

<div id="unity-lightbox">
    <a href="#" id="close-lighbox">
        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
    </a>
    <div id="lightbox-content-wrapper">
    <h1 id="sign-title"></h1>
    <div id="unityPlayer">
        <div class="missing">
            <a href="http://unity3d.com/webplayer/" title="Unity Web Player. Install now!">
                <img alt="Unity Web Player. Install now!"
                     src="/dictionary/unity/getunity.png" width="193" height="63"/>
            </a>
        </div>
    </div>
    <div id="share-box">
        <div class="form-group" id="input-group-perma-link">
            <label class="pull-left">PermaLink </label>
            <div class="controls">
                <input type="text" class="" readonly="readonly" id="input-perma-link">
            </div>
        </div>
    </div>
    </div>
</div>

