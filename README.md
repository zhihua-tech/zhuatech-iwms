# ZhuaTech IWMS｜企业不动产与设施管理系统

[简体中文](README.md) | [English](README.en.md)

> 统筹园区、空间、租赁、设施和服务，提升场所运营效率

ZhuaTech IWMS 是知华科技（上海如静知华信息科技有限公司）发布的企业级源码项目，面向“不动产、租赁、空间、工位、设施、维护、服务、能耗、项目与供应商”提供管理端与响应式业务端。工程采用前后端分离架构，所有示例数据均为虚构数据。

[知华科技官网](https://www.zhuatech.cn/) · [架构说明](docs/ARCHITECTURE.md) · [API 文档](docs/API.md) · [企业能力](docs/ENTERPRISE.md) · [测试说明](docs/TESTING.md)

![企业不动产与设施管理系统产品界面示意](docs/images/product-overview.svg)

## 业务模块

| 模块 | 核心能力 |
| --- | --- |
| 不动产台账 | 管理园区、楼宇、权属和估值信息 |
| 租赁管理 | 管理合同、租金、账期、续租和退租 |
| 空间管理 | 维护楼层、区域、面积、用途和容量 |
| 工位服务 | 支持工位、会议室和访客空间预订 |
| 设施设备 | 维护设备、位置、保修和关键等级 |
| 维护保养 | 生成计划、工单、点检和故障闭环 |
| 服务请求 | 受理保洁、安保、搬迁和行政服务 |
| 能源管理 | 采集水电气、分析强度并跟踪节能 |
| 改造项目 | 管理装修、搬迁和资本性改造 |
| 服务商管理 | 管理合同、人员、绩效和安全准入 |

![企业不动产与设施管理系统业务闭环](docs/images/workflow.svg)

## 企业级控制

- ADMIN / OPERATOR 角色边界和管理员接口隔离；
- 服务端字段、模块、唯一编号和状态迁移校验；
- 组织、期间、责任人、风险等级、到期日和 SLA 统计；
- 幂等创建、JPA 乐观锁、重复提交保护和职责分离；
- 附件 SHA-256 元数据、业务凭证完整性与全流程审计；
- 组合检索、分页、逾期筛选、UTF-8 CSV 导出和协作时间线；
- 外部系统仅预留适配器，使用方自行配置地址与凭据；
- prod profile 拒绝默认密码、弱数据库口令和本地跨域来源。

## 技术架构

- 后端：Java 21、Spring Boot、Spring Security、JPA、Bean Validation、Actuator
- 前端：Vue 3、Vite、Axios，支持桌面端与移动端响应式布局
- 数据库：MySQL 8；自动化测试使用 H2
- 交付：Docker Compose、Nginx、环境变量、GitHub Actions
- Java 包名：`cn.zhuatech.iwms`

## 启动与测试

```bash
cd backend && mvn test
cd ../frontend && npm install && npm run build
cd .. && cp .env.example .env && docker compose up --build
```

开发演示账号：`admin / admin123`、`operator / operator123`。生产环境必须通过环境变量替换全部默认凭据。

## 许可与商业授权

Copyright © 2026 上海如静知华信息科技有限公司。

本工程仅允许个人学习、研究和非商业技术交流，**不得用于商业用途**。企业内部使用、生产部署、SaaS运营、项目交付、品牌替换、收费培训、咨询实施或再分发，均须事先获得上海如静知华信息科技有限公司书面授权，详见 [LICENSE](LICENSE)。

深度开发、私有化部署、系统集成与企业数字化咨询，请访问[知华科技官网](https://www.zhuatech.cn/)或扫码联系：

| 微信咨询一 | 微信咨询二 |
| --- | --- |
| ![微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

SEO：企业不动产与设施管理系统、IWMS系统源码、企业数字化、Java企业系统、Vue管理系统、知华科技、上海如静知华信息科技有限公司。

## V2.0 专业设施运维域

新增设施资产、预防性保养计划、维护工单、完工验收与服务 SLA 模型。工单按照分派、开工、完工、验收推进，设备读数禁止回退；P1—P4 服务请求按优先级自动计算时限并支持超时升级。前端“设施运维中心”和 `/api/facility-ops` 接口提供专业入口。
