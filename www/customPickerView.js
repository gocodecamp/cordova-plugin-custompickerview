var exec = require("cordova/exec");

function PickerViewModel() {};

PickerViewModel.prototype.showPickerView = function (success,fail,option) {
     exec(success, fail, 'customPickerView', 'showPickerView', option);
};
PickerViewModel.prototype.showAddressPickerView = function (success,fail,option) {
exec(success, fail, 'customPickerView', 'showAddressPickerView', option);
};

var pickerViewModel = new PickerViewModel();
module.exports = pickerViewModel;


//示例:
//var dataArray = [{'name' : '北京', 'objectArray' : [{'name': '北京', 'objectArray': [{'name': '昌平'}, {'name': '海淀'}, {'name': '朝阳'}]}]},
//                 {'name' : '天津', 'objectArray' : [{'name': '天津', 'objectArray': [{'name': 'aaa'}, {'name': 'bbb'}, {'name': 'ccc'}]}]}];

//pickerViewModel.showPickerView(alertSuccess,alertFail,[{"dataArray" : dataArray, "columnCount": 3, "isLinkWork": true}]);

//callback返回的数据结构是 [{'name': '', 'objectArray': []}, {{'name': '', 'objectArray': []}, {{'name': '', 'objectArray': []}]