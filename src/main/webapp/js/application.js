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
    });
    $urlRouterProvider.otherwise("/");
});

app.controller("RootCtrl", ["$scope", "$rootScope", "$stateParams", "$state", "FS",
                            "$mdDialog", "Toast", "$window", "Move", "Copy", function(
                            $scope, $rootScope, $stateParams, $state, FS, $mdDialog,
                            Toast, $window, Move, Copy) {

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
                console.log("Ctrl + C")
                $rootScope.buffer.copy = $scope.selected.slice(0);
                $rootScope.buffer.cut = [];
                if($rootScope.buffer.copy.length > 0) {
                    Toast.show("200", $rootScope.buffer.copy.length +
                    " items successful copy to buffer")
                }
            }
            if ($event.keyCode == $scope.xKey && $scope.ctrlDown) {
                console.log("Ctrl + X")
                $rootScope.buffer.cut = $scope.selected.slice(0);
                $rootScope.buffer.copy = [];
                if($rootScope.buffer.cut.length > 0) {
                    Toast.show("200", $rootScope.buffer.cut.length +
                    " items successful copy to buffer")
                }
            }
            if ($event.keyCode == $scope.vKey && $scope.ctrlDown) {
                console.log("Ctrl + V")
                Move.send({p: $scope.fs.path}, $rootScope.buffer.cut)
                .$promise.then(function(response){
                    if(response.length > 0) {
                        Toast.show("200", response.length + " items successful paste to folder")
                    }
                    $rootScope.buffer.cut = []
                }, function(error) {
                    Toast.show(error.code, error.data.message)
                })

                Copy.send({p: $scope.fs.path}, $rootScope.buffer.copy)
                .$promise.then(function(response){
                    if(response.length > 0) {
                        Toast.show("200", response.length + " items successful paste to folder")
                    }
                    $rootScope.buffer.cut = []
                }, function(error) {
                    Toast.show(error.code, error.data.message)
                })
                $state.reload()
            }
        });


    var openFolder = function(path) {
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
    }

    var isOpen = false;

    var openFile = function(file, event) {
        if(isOpen) return;
        isOpen = true;
        FS.get({p:file.path}).$promise.then(function(response) {
            $mdDialog.show({
               templateUrl: 'views/dialogs/editfile.html',
               parent: angular.element(document.body),
               targetEvent: event,
               controller: function($scope, $mdDialog) {
                   $scope.file = response;
                   isOpen = false;
                   $scope.cancel = function() {
                       $mdDialog.cancel()
                   }
                   $scope.save = function(file) {
                        file.$update({p: file.path})
                        .then(function(response) {
                            Toast.show("200", "Successful updated")
                            $mdDialog.cancel();
                        }, function(error) {
                            Toast.show(error.status, error.data.message)
                        })
                   }
               }
            });
        }, function(error) {
            Toast.show(error.status, error.data.message)
        });
    }

    $scope.open = function(file, event) {
        if(file.directory) openFolder(file.path)
        else openFile(file, event)
        $scope.selected = []
    }

    openFolder($stateParams.p);
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
          var newfs = new FS({name: formData, directory: true})
          newfs.$save({p: $scope.fs.path}).then(function(response) {
            $scope.fs.files.push(newfs);
            Toast.show("201", "Folder successful added")
          }, function(error) {
            Toast.show(error.status, error.data.message)
          })
        }, function() {
        });
      };

      $scope.addFileDialog = function(ev) {
          var confirm = $mdDialog.prompt()
            .textContent('Please type new file name')
            .placeholder('File name')
            .ariaLabel('File name')
            .initialValue('Empty File')
            .targetEvent(ev)
            .ok('Create')
            .cancel('Cancel');

          $mdDialog.show(confirm).then(function(formData) {
            var newfs = new FS({name: formData})
            newfs.$save({p: $scope.fs.path}).then(function(response) {
              $scope.fs.files.push(newfs);
              Toast.show("201", "File successful added")
            }, function(error) {
              Toast.show(error.status, error.data.message)
            })
          }, function() {
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
          updateDir.$update({p: updateDir.path})
          .then(function(response) {
            dir.name = response.name
            dir.path = response.path
            Toast.show("200", "Successful updated")
          }, function(error) {
            Toast.show(error.status, error.data.message)
          })
        }, function() {
        });
      };

    $scope.removeFolder = function(index, dir) {
        var confirm = $mdDialog.confirm()
              .title('Would you like to delete file?')
              .textContent('All of the included data will be removed')
              .ok('Delete')
              .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
          FS.remove({p: dir.path}).$promise.then(function(response){
            $scope.fs.files = $scope.fs.files.filter(function(item) {
                return item.path != dir.path;
            });
            Toast.show("204", "Successful removed")
          }, function(error){
            Toast.show(error.status, error.data.message)
          })
        }, function() {
        });
    }
}])

app.factory("FS", ["$resource", function($resource){
    return $resource('/fs', {}, {'update':{method:'PUT'}})
}]);

app.factory("Move", ["$resource", function($resource){
    return $resource('/fs/move', {}, {'send':{method:'POST', isArray: true}})
}]);

app.factory("Copy", ["$resource", function($resource){
    return $resource('/fs/copy', {}, {'send':{method:'POST', isArray: true}})
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