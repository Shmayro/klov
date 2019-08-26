<#assign Math=statics['java.lang.Math']>
<#assign Utils=statics['com.aventstack.klov.utils.Utils']>
<!DOCTYPE html>
<html lang="en" ng-app="Klov">
    <#include 'partials/head.ftl'>
    <style type="text/css">
         .mt-70 {
         margin-top: 70px !important;
         }
        .green-graph {
    		background-color: #33cabb;
    		color: rgba(255, 255, 255, 0.87);
		}
		.red-graph {
    		background-color: #f96868;
    		color: rgba(255, 255, 255, 0.87);
		}
		.yellow-graph {
    		background-color: #fdd501;
    		color: rgba(0, 0, 0, 0.87);
		}
         .mt-32 {
         margin-top: 32px !important;
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
         margin-left: .5rem!important;
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
         position:absolute;
         bottom:0;
         right:25px;
         }
    </style>
    <body>
        <div class="app" id="app">
            <#include 'partials/sidenav.ftl'>
            <!-- content -->
            <div id="content" class="app-content box-shadow-z2 bg pjax-container" role="main" ng-controller="TestController">
                <div class="app-header white bg b-b">
                    <div class="navbar" data-pjax>
                        <a data-toggle="modal" data-target="#aside" class="navbar-item pull-left hidden-lg-up p-r m-a-0">
                        <i class="material-icons">reorder</i>
                        </a>
                        <div class="navbar-item pull-left h5" id="pageTitle">Tests</div>
                        <#include 'partials/navbar-right.ftl'>
                    </div>
                </div>
                <#include 'partials/footer.ftl'>
                <div class="app-body">
                    <!-- ############ PAGE START-->
                    <div class="padding">
                    	<div class="row m-b">
        					<div class="col-sm-2">
        						<div class="row m-b">
        							<form>
									  	<div class="form-group">
										    <label for="categories">Version</label>
										    <select class="form-control" id="categories" onchange="location = this.value;">
										      <option value="/tests" >All Versions</option>
										      <#list categoryList as category>
	        									<#if RequestParameters.category?? && RequestParameters.category == category>
	        										<option value="/tests?category=${category}" selected>${category}</option>
	        									<#else>
	        										<option value="/tests?category=${category}">${category}</option>
	        									</#if>	
											  </#list>
											</select>
										</div>
										
										  <#if RequestParameters.category??>
											  <div class="form-group">
											  	<label for="features">Feature</label>
											  	<select class="form-control" id="features" onchange="location = this.value;">
											  	  <option value="/tests?category=${RequestParameters.category}">All Features</option>
											      <#list nameList as name>
		        									<#if RequestParameters.name?? && RequestParameters.name == name>
		        										<option value="/tests?category=${RequestParameters.category}&name=${name}" selected>${name}</option>
		        									<#else>
		        										<option value="/tests?category=${RequestParameters.category}&name=${name}">${name}</option>
		        									</#if>	
												  </#list>
												</select>
											  </div>
										  </#if>
									   
									  
									</form>
								</div>
        					</div>
        					<div class="col-sm-10">
		                        <div class="row m-b">
		                            <#list testList as test> <#assign report=reportMap[test.report]> <#if (report.name)??>
		                            	
		                            	<#assign featurePassed=0>
				                        <#assign featureFailed=0>
				                        <#assign featureOthers=0>
		                            	
		                            	<#if (report.parentLength)?? && report.parentLength gt 0>
				                            <#assign featurePassed=Math.round((report.passParentLength/report.parentLength)*100)>
				                            <#assign featureFailed=Math.round(((report.failParentLength+report.fatalParentLength+report.errorParentLength)/report.parentLength)*100)>
				                            <#assign featureOthers=Math.round(((report.skipParentLength+report.warningParentLength)/report.parentLength)*100)>
			                            </#if>
			                            
			                            <div class="col-sm-4" ng-if="!r${test?counter}">
			                                <div class="box">
			                                    <div class="box-header">
			                                        <h3>${test.name?replace('qaa_SanityCheck_','')}</h3>
			                                        <small>${prettyTime.format(test.startTime)}</small>
			                                        <small>${test.startTime?datetime}</small>
			                                        <div>
			                                        	<#if test.categorized??>
			                                        		<#list test.categoryNameList as category>
			                                        			<span class="label blue-grey"><i class="fa fa-tag"></i> &nbsp; ${category}</span>
															</#list>
														<#else>
															<span class="label blue-grey"><i class="fa fa-tag"></i> &nbsp; ${report.name}</span>
				                                        </#if>
			                                        </div>
			                                        
			                                    </div>
			                                    <div class="box-tool">
			                                        <ul class="nav">
			                                            <li class="nav-item inline">
			                                                <a class="nav-link" alt="View all tests" title="View all tests" href="/test?id=${test.id}">
			                                                <button class="btn btn-icon white"><i class="material-icons">input</i></button>
			                                                </a>
			                                            </li>
			                                            <li class="nav-item inline">
			                                                <a class="nav-link" alt="View failed tests" title="View failed tests" href="/test?id=${test.id}&status=fail">
			                                                <button class="btn btn-icon white"><i class="material-icons">report</i></button>
			                                                </a>
			                                            </li>
			                                            <!-- if admin -->
			                                            <#if user?? && user.admin>
			                                            <li class="nav-item inline">
			                                                <a class="nav-link" ng-click="remove('${test.id}', 'r${test?counter}')" href="#" alt="Delete test" title="Delete test">
			                                                <button class="btn btn-icon white"><i class="material-icons">delete</i></button>
			                                                </a>
			                                            </li>
			                                            </#if>
			                                        </ul>
			                                    </div>
			                                    <div class="box-body">
			                                        
			                                        <div>
			                                            <div class="peity-chart" data-provide="peity" data-type="bar" data-height="70" data-width="90" data-fill="#33cabb,#48b0f7,#fdd501">${featurePassed},${featureFailed},${featureOthers}</div>
			                                            <ul class="pull-right list-inline align-self-end text-muted text-right mb-0">
			                                                <#if featurePassed gt 0><li>${featurePassed}% Pass &nbsp; <span class="badge badge-ring badge-primary ml-2"></span></li></#if>
			                                                <#if featureFailed gt 0><li>${featureFailed}% Failed &nbsp; <span class="badge badge-ring badge-danger ml-2"></span></li></#if>
			                                                <#if featureOthers gt 0><li>${featureOthers}% Others &nbsp; <span class="badge badge-ring badge-yellow ml-2"></span></li></#if>
			                                            </ul>
			                                            <#if (report.parentLength)?? && report.parentLength gt 0>
			                                            	<ul class="pull-left list-inline align-self-start text-right mb-0">
			                                                	<#if report.parentLength gt 0><li><span class="label green-graph">${report.passParentLength}/${report.parentLength} pass</span></li></#if>
			                                                	<#if (report.failParentLength+report.fatalParentLength+report.errorParentLength) gt 0><li><span class="label red-graph">${report.failParentLength+report.fatalParentLength+report.errorParentLength}/${report.parentLength} failed</span></li></#if>
			                                                	<#if (report.skipParentLength+report.warningParentLength) gt 0><li><span class="label yellow-graph">${report.skipParentLength+report.warningParentLength}/${report.parentLength} others</span></li></#if>
			                                            	</ul>
			                                            <#else>
			                                            	<ul class="pull-left list-inline align-self-start text-right mb-0">
			                                            		<li><span class="label red-graph">Test Interupted</span></li>
			                                            	</ul>
			                                            </#if>
			                                        </div>
			                                        
			                                    </div>
			                                </div>
			                            </div>
		                            </#if></#list>
		                        </div>
		                	</div>
		               	</div>
                        <ul class="pagination">
                            <!-- previous, next -->
                            <#assign disabled="">
                            <#assign url="/tests?">
                            <#if RequestParameters.category?? >
                            	<#assign url=url+"&category="+RequestParameters.category >
                            </#if>
                            <#if RequestParameters.name?? >
                            	<#assign url=url+"&name="+RequestParameters.name >
                            </#if>
                            <#if page==0><#assign disabled="disabled"></#if>
                            <li class="page-item ${disabled}">
                                <a class="page-link" href="${url}&page=0" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                                <span class="sr-only">Previous</span>
                                </a>
                            </li>
                            <#list page-3..page+2 as x>
                            	<#if x gte 0 && (x lte pages-1) ><li class="page-item"><a class="page-link" href="${url}&page=${x}">${x+1}</a></li></#if>
                            </#list>
                            <#assign disabled="">
                            <#if page==pages-1><#assign disabled="disabled"></#if>
                            <li class="page-item ${disabled}">
                                <a class="page-link" href="${url}&page=${pages-1}" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                                <span class="sr-only">Next</span>
                                </a>
                            </li>
                        </ul>
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
            	height: 70,
            	width: 90,
            	fill: [ "#33cabb" ,"#f96868", "#fdd501" ]
            });
        </script>
    </body>
</html>