console.info("Application was started!")

var app = angular.module("app", [
    "ui.router",
    "ngResource",
    "ui.router",
    "ngMaterial",
    "ngMessages",
    "ngAnimate",
    "ncy-angular-breadcrumb"
])

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
    .state("root", {
        url: "/?p&f",
        templateUrl: "views/root.html",
        controller: "RootCtrl",
    })

    $urlRouterProvider.otherwise("/");

});

app.controller("RootCtrl", ["$scope", "$rootScope", "$stateParams", "$state", "FS",
"$mdDialog", "Toast", "$window",
function($scope, $rootScope, $stateParams, $state, FS, $mdDialog, Toast, $window){

    $scope.ctrlDown = false;
    $scope.ctrlKey = 17, $scope.vKey = 86, $scope.cKey = 67; $scope.xKey = 88;

    angular.element($window).bind("keyup", function($event) {
        if ($event.keyCode == $scope.ctrlKey)
            $scope.ctrlDown = false;
        $scope.$apply();
    });

    angular.element($window).bind("keydown", function($event) {
        if ($event.keyCode == $scope.ctrlKey)
            $scope.ctrlDown = true;
        $scope.$apply();
    });

    $rootScope.buffer = {copy: [], cut: []}

    angular.element($window).bind("keydown", function($event) {
            if ($event.keyCode == $scope.cKey && $scope.ctrlDown) {
                $rootScope.buffer.copy = $scope.selected;
                $rootScope.buffer.cut = [];
                if($rootScope.buffer.copy.length > 0) {
                    Toast.show("200", $rootScope.buffer.copy.length +
                    " items successful copy to buffer")
                }
            }
            if ($event.keyCode == $scope.xKey && $scope.ctrlDown) {
                $rootScope.buffer.cut = $scope.selected;
                $rootScope.buffer.copy = [];
                if($rootScope.buffer.cut.length > 0) {
                    Toast.show("200", $rootScope.buffer.cut.length +
                    " items successful copy to buffer")
                }
            }
            if ($event.keyCode == $scope.vKey && $scope.ctrlDown) {
                // TODO paste
            }
        });

    $scope.openFolder = function(path) {
        FS.get({p:path}).$promise.then(function(response) {
            $scope.fs = response;
            $stateParams.p = path;
            $state.go('root', $stateParams, {notify: false});
        }, function(error) {
            $scope.fs = FS.get();
            $stateParams.p = undefined;
            $state.go('root', $stateParams, {notify: false});
            Toast.show(error.status, error.data.message)
        });
        $scope.selected = []
    }

    $scope.openFolder($stateParams.p);
    $scope.selected = [];

    $scope.toggleSelect = function(event, item) {
        var t = $(event.target)
        if(t.hasClass("selected")) {
            t.removeClass("selected")
            $scope.selected = $scope.selected.filter(function(i){
                return item.name != i.name && item.path != i.path;
            })
        } else {
            t.addClass("selected")
            $scope.selected.push(item)
        }
    }

    $scope.addFolderDialog = function(ev) {
        var confirm = $mdDialog.prompt()
          .textContent('Please type new folder name')
          .placeholder('Folder name')
          .ariaLabel('Folder name')
          .initialValue('New Folder')
          .targetEvent(ev)
          .ok('Create')
          .cancel('Cancel');

        $mdDialog.show(confirm).then(function(formData) {
          var newfs = new FS({name: formData, directories: []})
          newfs.$save({p: $scope.fs.path}).then(function(response) {
            $scope.fs.directories.push(newfs);
            Toast.show("201", "Folder successful added")
          }, function(error) {
            Toast.show(error.status, error.data.message)
          })
        }, function() {
          console.log("cancel")
        });
      };

       $scope.renameFolderDialog = function(dir) {

        var confirm = $mdDialog.prompt()
          .textContent('Please type new name')
          .placeholder('Folder name')
          .ariaLabel('Folder name')
          .initialValue(dir.name)
          .ok('Update')
          .cancel('Cancel');

        $mdDialog.show(confirm).then(function(formData) {
          updateDir = new FS(dir);
          updateDir.name = formData;
          updateDir.$update({p:updateDir.path})
          .then(function(response) {
            dir.name = response.name
            dir.path = response.path
            Toast.show("200", "Folder successful updated")
          }, function(error) {
            Toast.show(error.status, error.data.message)
          })
        }, function() {
          console.log("cancel")
        });
      };

    $scope.removeFolder = function(index, dir) {
        var confirm = $mdDialog.confirm()
              .title('Would you like to delete folder?')
              .textContent('All of the directories and files will be removed')
              .ok('Delete')
              .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
          FS.remove({p: dir.path}).$promise.then(function(response){
            $scope.fs.directories.splice(index, 1);
            Toast.show("204", "Folder successful removed")
          }, function(error){
            Toast.show(error.status, error.data.message)
          })
        }, function() {
          console.log("cancel")
        });
    }

}])

app.factory("FS", ["$resource", function($resource){
    return $resource('/fs', {}, {'update':{method:'PUT'}})
}]);


app.service('Toast', ['$mdToast', function($mdToast) {
  this.show = function(status, message, timeout=1000) {
    $mdToast.show({
      hideDelay   : timeout,
      locals      : {status: status, message: message},
      controller  : 'ToastController',
      templateUrl : '/views/dialogs/toast.html',
      position    : 'bottom left',
    });
  }
}]).controller("ToastController", ['$scope', '$mdToast', '$state', 'status', 'message',
function($scope, $mdToast, $state, status, message){
  if(status == 401) { $state.reload() }
  $scope.status = status.toString()[0];
  $scope.message = message;
  $scope.close = function() { $mdToast.hide() };
}]);

Array.prototype.foreach = function (callback) {
  iterations = Math.floor(this.length / 5);
  len = this.length
  if(len == 0) {return this}
  dot = len % 5;
  i = 0;
  do { switch (dot) {
      case 0: callback(this[i++]);
      case 4: callback(this[i++]);
      case 3: callback(this[i++]);
      case 2: callback(this[i++]);
      case 1: callback(this[i++]);
    }
    dot = 0
  } while (iterations--)
  return this;
};
