//
//  customPickerViewPlugin.m
//
//  Created by DLonion on 16/9/20.
//
//

#import "ZJCustomPickerViewPlugin.h"
#import "CoreActionSheetPicker.h"

@interface ZJCustomPickerViewPlugin () <ActionSheetCustomPickerDelegate>
    
    @property (nonatomic,strong) NSArray *addressArr; // 解析出来的最外层数组
    
    @property (nonatomic,strong) ActionSheetCustomPicker *picker; // 选择器
    
    @property (nonatomic,strong) NSMutableArray *objectArr;
    @property (nonatomic,strong) NSMutableArray<NSNumber *> *indexArray;
    @property (nonatomic,strong) NSMutableArray<NSArray<NSDictionary *> *> *contentArray;
    @property (nonatomic,assign) BOOL isLinkWork;
    
    @property (nonatomic, strong) NSString *callbackId;
    
    
    
    @end

@implementation ZJCustomPickerViewPlugin
    
-(void)showAddressPickerView:(CDVInvokedUrlCommand *)command{
    self.callbackId = command.callbackId;
    
    NSData *JSONData = [NSData dataWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"addr" ofType:@"json"]];
    
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:JSONData options:NSJSONReadingAllowFragments error:nil];
    
    NSArray *array = dict[@"dataArray"];
    [self showPickerWith:array componentCount:3 isLinkWork:YES title:@"选择地区"];
    
    
}
    
-(void)showPickerView:(CDVInvokedUrlCommand *)command{
    
    self.callbackId = command.callbackId;
    
    if (command.arguments.count >0) {
        NSDictionary *dict = command.arguments[0];
        NSArray *array = dict[@"dataArray"];
        NSNumber *isLinkWorkNumber = dict[@"isLinkWork"];
        NSNumber *columnCount = dict[@"columnCount"];
        NSString *title = dict[@"title"];
        [self showPickerWith:array componentCount:columnCount.intValue isLinkWork:isLinkWorkNumber.intValue title:title];
    }else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"没有参数"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
    
    
    
}
    
    
- (void)showPickerWith:(NSArray *)objectArray componentCount:(int)componentCount isLinkWork:(BOOL)isLinkWork title:(NSString *)title{
    
    self.indexArray = nil;
    for (int i = 0; i<componentCount; i++) {
        [self.indexArray addObject:@0];
    }
    self.isLinkWork = isLinkWork;
    self.objectArr = [NSMutableArray arrayWithArray:objectArray];
    
    [self calculateData];
    
    self.picker = [[ActionSheetCustomPicker alloc]initWithTitle:title delegate:self showCancelButton:YES origin:[UIApplication sharedApplication].keyWindow initialSelections:self.indexArray];
    self.picker.tapDismissAction  = TapActionSuccess;
    [self.picker showActionSheetPicker];
}
    
    
    
    // 根据下标数组计算对应的三个数组
- (void)calculateData
    {
        
        if (self.isLinkWork) {
            int count = (int)self.indexArray.count;
            
            [self.contentArray removeAllObjects];
            [self.contentArray addObject:self.objectArr];
            
            NSArray *tempArray = self.objectArr;
            for (int i = 0; i<count-1; i++) {
                NSNumber *indexNumber = self.indexArray[i];
                NSDictionary *dict = tempArray[indexNumber.intValue];
                tempArray = dict[@"objectArray"];
                
                if (tempArray) {
                    [self.contentArray addObject:tempArray];
                }else {
                    [self.contentArray addObject:@[]];
                }
                
            }
        }else {
            self.contentArray = self.objectArr;
        }
        
    }
    
    
#pragma mark - UIPickerViewDataSource Implementation
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
    {
        return self.contentArray.count;
    }
    
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
    {
        
        return self.contentArray[component].count;
    }
    
    
#pragma mark UIPickerViewDelegate Implementation
    
    
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
    {
        return self.contentArray[component][row][@"name"];
    }
    
- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view
    {
        UILabel* label = (UILabel*)view;
        if (!label)
        {
            label = [[UILabel alloc] init];
            [label setFont:[UIFont systemFontOfSize:14]];
        }
        
        NSString * title = @"";
        
        title = self.contentArray[component][row][@"name"];
        label.textAlignment = NSTextAlignmentCenter;
        label.text=title;
        return label;
    }
    
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
    {
        
        if (self.isLinkWork) {
            for (int i = 0; i<self.indexArray.count; i++) {
                if (component == i) {
                    self.indexArray[i] = [NSNumber numberWithInteger:row];
                    int count = (int)(self.indexArray.count - component);
                    
                    for (int j = 1; j < count; j++) {
                        self.indexArray[i+j] = @0;
                        [self calculateData];
                        [pickerView reloadComponent:i+j];
                        [pickerView selectRow:0 inComponent:i+j animated:YES];
                    }
                    break;
                }
            }
        }else {
            self.indexArray[component] = [NSNumber numberWithInteger:row];
            
        }
        
        
    }
    
    
- (void)configurePickerView:(UIPickerView *)pickerView
    {
        pickerView.showsSelectionIndicator = NO;
    }
    // 点击done的时候回调
- (void)actionSheetPickerDidSucceed:(ActionSheetCustomPicker *)actionSheetPicker origin:(id)origin
    {
        NSMutableArray *doneArray = [NSMutableArray array];
        
        for (int i=0; i<self.contentArray.count; i++) {
            NSArray *array = self.contentArray[i];
            if (array.count > 0) {
                [doneArray addObject:array[self.indexArray[i].intValue]];
            }
            
        }
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:doneArray];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
        NSLog(@"%@", doneArray);
    }
    
#pragma mark - getter
    
-(NSArray *)addressArr
    {
        if (_addressArr == nil) {
            _addressArr = [[NSArray alloc] init];
        }
        return _addressArr;
    }
    
-(NSMutableArray *)indexArray
    {
        if (_indexArray == nil) {
            _indexArray = [[NSMutableArray alloc] init];
        }
        return _indexArray;
    }
    
-(NSMutableArray *)contentArray
    {
        if (_contentArray == nil) {
            _contentArray = [[NSMutableArray alloc] init];
        }
        return _contentArray;
    }
    
-(NSMutableArray *)objectArr
    {
        if (_objectArr == nil) {
            _objectArr = [[NSMutableArray alloc] init];
        }
        return _objectArr;
    }
@end
