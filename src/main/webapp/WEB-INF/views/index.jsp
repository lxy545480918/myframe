<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>hi</title>
    <link rel="shortcut icon" href="/content/image/favicon.ico">
    <link type="text/css" rel="stylesheet" href="/content/css/about.css">
    <script type="text/javascript"src="/content/script/lib/jquery-1.8.3.min.js"></script>
    <script type="text/javascript"src="/content/script/lib/jquery.mousewheel.min.js"></script>
    <script type="text/javascript">
        var page=0;//翻屏变量，初始第一屏
        var shakStaute = 0; //该变量作用是鼠标滑轮一直向下或者向上滑动时出现抖动现象
        $(function(){

            var starttime = 0,
                    endtime = 0;
            $("body").mousewheel(function(event, delta) {
                starttime = new Date().getTime(); //记录翻屏的初始时间
                if (delta < 0&& page>=0 && page<=$(".content .divsame").length-2) {
                    if (shakStaute>=0 &&(starttime == 0 || (endtime - starttime) <= -500)) { //在500ms内执行一次翻屏
                        shakStaute=1;
                        page++;
                        renderPage(page,true);  //翻屏函数
                        endtime = new Date().getTime();    //记录翻屏的结束时间
                    }
                } else if (delta>0 && page>=1 && shakStaute==1 && (starttime == 0 || (endtime - starttime) <= -500)) {
                    page--;
                    renderPage(page,true);
                    endtime = new Date().getTime();
                }
            });
            var div_height=$(window).height();
            $(".divsame").css({'height':div_height});

            $(window).resize(function(){

                div_height=$(window).height();

                $(".divsame").css({'height':div_height});
                $('.content').animate({top:-page*div_height }, 100);
            });
            $(".left_fixed ul li").on("click", function(){ //点击小导航也执行翻屏
                var index = $(this).index();
                if(index>0){
                    shakStaute==1;
                }
                page = index;
                renderPage(page, true);
                $(".left_fixed ul li").removeClass("active");
                $(this).addClass("active");
                return false;
            });
            function renderPage(pageNumber, isScroll){
                if (isScroll){
                    $('.content').animate({top:-pageNumber*div_height }, 'slow');

                    var activeDiv = pageNumber + 1;
                    $('.content .divsame').removeClass("active");
                    $('.content .div_0' + activeDiv + '').addClass("active");


                    $(".left_fixed ul li").removeClass("active");
                    $(".left_fixed ul li").eq(pageNumber).addClass("active");
                }
                return;
            }

            $('.content .div_01').addClass("active");
        })
    </script>
</head>
<body>
<div class="content">
    <div class="div_01 divsame">
        <div class="div-content div01-content">

        </div>
    </div>
    <div class="div_02 divsame">
        <div class="div-content div02-content">

        </div>
    </div>
    <div class="div_03 divsame">
        <div class="div-content div03-content">

        </div>
    </div>
    <div class="div_04 divsame">
        <div class="div-content div04-content">

        </div>
    </div>
</div>
<div class="left_fixed">
    <ul>
        <li class="active"></li>
        <li></li>
        <li></li>
        <li></li>
    </ul>
</div>
</body>
</html>