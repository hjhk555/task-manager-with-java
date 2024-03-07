# 任务管理器：日常小管家

这是一款能够帮助您实现任务记录、管理、查阅以及按时提醒的小工具。同时它还能检测鼠标运动，当您在电脑前久坐时提醒您，为您的健康保驾护航。

## 任务

### 任务新建

您可以使用“主菜单”——“新建任务”选项新建您的任务日志，或是使用快捷键（Ctrl+N）。其中可以填写任务的名称、详情，选择任务的类型、时限，或是关闭任务到期的警报。任务有如下4个种类：

- 单次任务

一次性的任务，会在设定好的时间提醒，并在完成后留档。例如临时追加的限期工作。

- 不限时任务

一次性的任务，不过没有时间限制，因此不会提醒。通常用于记录灵感或是备忘。

- 每天更新任务

每日要做的任务，会在指定的时刻刷新并提醒。例如下班打卡（bushi）。

- 每周更新任务

每周要做的任务，会在每周指定的日子刷新并提醒，例如LeetCode的周赛。

### 任务查阅/修改

添加或留档的任务会出现在主页面，双击任务条目可以查看详情。右击任务条目打开右键菜单，您可以在此完成指定任务、删除指定任务、或是对任务内容与类型进行修改。

对任务的误操作可以通过“主菜单”——“撤销”（快捷键Ctrl+Z）撤销。撤销的操作也能通过“主菜单”——“重做”（快捷键Ctrl+Y）恢复。

### 任务导出

您可以通过“主菜单”——“导出任务”将目前任务及其状态导出为Task List文件（*.tlist）。文件会进行一定程度的加密以保护隐私（至少直接打开文件只能看到乱码）。

导出的默认文件名可以通过“主菜单”——“设置”——“默认导出文件名”修改。

### 任务导入

您可以通过“主菜单”——“导入任务”将任务文件导入，这样做会打开任务导入界面。在此界面，您可以查看文件内含任务的详情，并选择（按住Ctrl多选）想要导入的任务。

### 任务临近提醒

当单次任务临近时，程序会在主界面的任务情况栏发出提示，同时相关任务前会出现五角星。提示分为3个阶段：

- ★：剩余7天，只显示在任务前，不在任务情况栏出现。
- ★★：剩余3天，同时显示在任务前和任务情况栏中。
- ★★★：剩余1天，同时显示在任务前和任务情况栏中。

您可以通过“主菜单”——“设置”——“任务临近”修改临近提醒期限。

## 久坐提醒

程序会不断检测鼠标运动，若鼠标持续运动超过一定时间，会提示您起身活动。活动一定时间后相关提示解除。

您可以通过“主菜单”——“设置”调整相关配置：
- “连续使用提示阈值”：设置连续使用电脑的最长时间。
- “离开计时阈值”：设置所需活动时长。
- “鼠标活动误差”：用于防止鼠标“漂移”导致离开时被算作使用电脑，当每秒鼠标运动小于设定的像素时视为离开。

## 提醒框

当有任务到期或是检擦到久坐时，提醒框会弹出以提醒。提醒框拥有最高对话框优先级，以保证大部分情况下其不会被覆盖。提醒框弹出一段时间后，若任务仍未完成，会更新提醒并再此次弹出。

若存在紧急事项，需要暂停提醒，可以通过主界面的“暂停警报”按钮暂停警报30分钟。

您可以通过“主菜单”——“设置”调整相关配置：
- “提示间隔”：两次提醒之间的延时。
- “提示暂停时长”：暂停提醒的时长。

# 祝您使用愉快😀