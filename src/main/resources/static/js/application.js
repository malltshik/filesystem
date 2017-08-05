console.info("Application was started!")

var app = angular.module("app", [
    "ui.router",
    "ngResource",
])

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
    .state("root", {
        url: "/",
        templateUrl: "views/root.html",
        controller: "RootCtrl",
    })

    $urlRouterProvider.otherwise("/");

});

app.controller("RootCtrl", ["$scope", "$rootScope", function($scope, $rootScope){

    $scope.name = "World"

}])

