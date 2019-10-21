<#assign Math=statics['java.lang.Math']>
<!DOCTYPE html>
<html lang="en" ng-app="Klov">
<#setting time_zone="UTC">
<#include 'partials/head.ftl'>
<style type="text/css">
    .mt-70 {
        margin-top: 70px !important;
    }

    .green {
        background-color: #33cabb;
        color: rgba(255, 255, 255, 0.87);
    }

    .red {
        background-color: #f96868;
        color: rgba(255, 255, 255, 0.87);
    }

    .yellow {
        background-color: #fdd501;
        color: rgba(0, 0, 0, 0.87);
    }

    .badge-primary {
        background-color: #33cabb;
    }

    .badge-info {
        background-color: #48b0f7;
    }

    .badge-yellow {
        background-color: #fcc525;
    }

    .badge-danger {
        background-color: #f96868
    }

    .badge {
        border-radius: 3px;
        font-weight: 400;
        line-height: 1.3;
        font-size: 85%;
    }

    .ml-2 {
        margin-left: .5rem !important;
    }

    .badge-primary {
        color: #fff;
        background-color: #007bff;
    }

    .badge {
        display: inline-block;
        padding: .25em .4em;
        font-size: 75%;
        font-weight: 700;
        line-height: 1;
        color: #fff;
        text-align: center;
        white-space: nowrap;
        vertical-align: baseline;
        border-radius: .25rem;
    }

    .badge:empty {
        display: inline-block;
        vertical-align: inherit;
    }

    .badge-ring {
        position: relative;
        width: 10px;
        height: 10px;
        padding: 0;
        border-radius: 100%;
        vertical-align: middle;
    }

    .badge-ring::after {
        content: '';
        position: absolute;
        top: 2px;
        left: 2px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: #fff;
        -webkit-transform: scale(1);
        transform: scale(1);
        -webkit-transition: .3s;
        transition: .3s;
    }

    .align-self-end {
        position: absolute;
        bottom: 0;
        right: 25px;
    }

    .last_reports td {
        white-space: nowrap;
    }
</style>
<body>
<div class="app" id="app">
    <#include 'partials/sidenav.ftl'>
    <!-- content -->
    <div id="content" class="app-content box-shadow-z2 bg pjax-container" role="main" ng-controller="ReportController">
        <div class="app-header white bg b-b">
            <div class="navbar" data-pjax>
                <a data-toggle="modal" data-target="#aside" class="navbar-item pull-left hidden-lg-up p-r m-a-0">
                    <i class="material-icons">reorder</i>
                </a>
                <div class="navbar-item pull-left h5" id="pageTitle">Builds</div>
                <#include 'partials/navbar-right.ftl'>
            </div>
        </div>
        <#include 'partials/footer.ftl'>
        <div class="app-body">
            <!-- ############ PAGE START-->
            <div class="padding">
                <div class="row">
                    <div class="col-12">
                        <div class="box">
                            <div class="box-header">
                                <span class="label success pull-right">0</span>
                                <h3>Last Reports Summary</h3>
                            </div>
                            <div class="box-body">
                                <table class="table last_reports">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <#list versions as version>
                                            <th>
                                                <select onchange="location = this.value;">
                                                    <#list similarVersionsMap[version] as similarVersion>
                                                        <option value="/lastReports?<#if dvs??>${dvs}</#if>dv=${similarVersion}"
                                                                <#if version == similarVersion>selected</#if>>${similarVersion}</option>
                                                    </#list>
                                                </select>
                                            </th>
                                        </#list>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <#list features as feature>
                                        <tr>
                                            <td>${feature.getTitle()}</td>
                                            <#list versions as version>
                                                <td>
                                                    <#if !stateByVersionByFeature[version][feature.getQueryName()]??>
                                                        <span class="label">not found</span>
                                                    <#else>
                                                        <#if stateByVersionByFeature[version][feature.getQueryName()].status == 'pass'>
                                                            <span class="label green">pass</span>
                                                        <#elseif stateByVersionByFeature[version][feature.getQueryName()].status == 'fail'>
                                                            <#assign report=reportMap[stateByVersionByFeature[version][feature.getQueryName()].report]>
                                                            <span class="label red"><#if report.parentLength?? && report.fatalParentLength?? && report.errorParentLength?? && report.parentLength??>${report.failParentLength+report.fatalParentLength+report.errorParentLength}/${report.parentLength}</#if> fail</span>
                                                        </#if>

                                                        <small title="${stateByVersionByFeature[version][feature.getQueryName()].endTime?datetime}">${prettyTime.format(stateByVersionByFeature[version][feature.getQueryName()].endTime)}</small>

                                                        <a alt="View all tests" title="View Test" target="_blank"
                                                           href="/test?id=${stateByVersionByFeature[version][feature.getQueryName()].id}">
                                                            <i class="fa fa-external-link"></i>
                                                        </a>

                                                    </#if>
                                                </td>
                                            </#list>
                                        </tr>
                                    </#list>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- / -->
    <#include 'partials/switcher.ftl'>
    <!-- ############ LAYOUT END-->
</div>
<#include 'partials/scripts.ftl'>
<#include 'partials/angular.ftl'>
<script>
    $(".peity-chart").peity("bar", {
        height: 130,
        width: 90,
        fill: ["#33cabb", "#f96868", "#fdd501"]
    });
</script>
</body>
</html>