angular.module('Klov')
    .controller('TestController', ['$rootScope', '$scope', '$http', '$location', '$sce', 'Icon', 'Color', 'Font', 
    function($rootScope, $scope, $http, $location, $sce, Icon, Color, Font) {
    	
    	$scope.trust = $sce.trustAsHtml;
    	
    	$scope.getColor = function(status) {
            return Color.getColor(status);
        };
        
        $scope.getBootstrapColor = function(status) {
            return Color.getBootstrapColor(status);
        };
        
        $scope.getFont = function(status) {
    		return Font.getFont(status);
        };
        
        $scope.getErrorScreen = function(details){
        	if(details.length>100){
        		return $(details)[2].outerHTML;
        	}
        	return "";
        };
        
        $scope.getErrorResume = function(details){
        	if(details.length>100){
        		return $(details)[0].textContent.split('\n')[0]+"\n"+$(details)[0].textContent.split('\n')[1];
        	}
        	return "";
        };
        
    	$scope.findTest = function(id, historical, resetView) {
    		if (typeof reserView === "undefined" || reserView === null || resetView === "")
    			reserView = true;
    		
			var req = {
                method: 'GET',
                url: '/rest/tests/deepPopulate',
                params: {
                    id: id
                }
            };

            //$http.defaults.headers.post['X-CSRF-Token'] = $rootScope._csrf;
            
            $http(req).
                success(function(res) {
                    if (!historical) {
                    	if (resetView)
                    		$scope.historicalTest = null;
                    	
                		$scope.historicalList = null;
                		
                		$scope.currentTest = res;
                    }
                    else {
                    	$scope.historicalTest = res;
                    }
                });
    	};
    	
    	$scope.findHistory = function(currentTest) {
    		var req = {
                method: 'GET',
                url: '/rest/tests/findHistory',
                params: {
                    id: currentTest.id
                }
            };

            //$http.defaults.headers.post['X-CSRF-Token'] = $rootScope._csrf;
            
            $http(req).
                success(function(res) {
                    $scope.historicalList = res;
                });
    	};
    	
    	$scope.getTime = function(ms) {
    		return new Date(ms).toLocaleTimeString();
	    };
    	
    }]);