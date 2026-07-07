package com.zhenyu.config;

import com.zhenyu.entity.AdminUser;
import com.zhenyu.entity.User;
import com.zhenyu.entity.Resume;
import com.zhenyu.entity.KnowledgeDoc;
import com.zhenyu.entity.InterviewSession;
import com.zhenyu.entity.InterviewMessage;
import com.zhenyu.entity.AssessmentReport;
import com.zhenyu.entity.JobPosition;
import com.zhenyu.repository.AdminUserRepository;
import com.zhenyu.repository.UserRepository;
import com.zhenyu.repository.ResumeRepository;
import com.zhenyu.repository.KnowledgeDocRepository;
import com.zhenyu.repository.InterviewSessionRepository;
import com.zhenyu.repository.InterviewMessageRepository;
import com.zhenyu.repository.AssessmentReportRepository;
import com.zhenyu.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final KnowledgeDocRepository knowledgeDocRepository;
    private final InterviewSessionRepository sessionRepository;
    private final InterviewMessageRepository messageRepository;
    private final AssessmentReportRepository reportRepository;
    private final JobPositionRepository jobPositionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
        initUsers();
        initResumes();
        initKnowledgeDocs();
        initJobPositions();
        initSessions();
        log.info("数据初始化完成");
    }

    private void initAdminUser() {
        if (adminUserRepository.count() == 0) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("超级管理员");
            admin.setRole("ADMIN");
            adminUserRepository.save(admin);
            log.info("初始化管理员账号: admin / admin123");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            String[] nicknames = {"张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十"};
            String[] phones = {"13800000001", "13800000002", "13800000003", "13800000004",
                             "13800000005", "13800000006", "13800000007", "13800000008"};

            for (int i = 0; i < nicknames.length; i++) {
                User user = new User();
                user.setNickname(nicknames[i]);
                user.setPhone(phones[i]);
                user.setOpenid("openid_" + (i + 1));
                user.setAvatarUrl("https://via.placeholder.com/100");
                user.setGender(i % 2 == 0 ? (short)1 : (short)0);
                user.setIsActive(true);
                user.setLastLoginTime(LocalDateTime.now().minusDays(i));
                userRepository.save(user);
            }
            log.info("初始化 {} 个用户", nicknames.length);
        }
    }

    private void initResumes() {
        if (resumeRepository.count() == 0) {
            var users = userRepository.findAll();
            String[] fileNames = {"Java开发工程师简历.pdf", "前端开发工程师简历.docx", "产品经理简历.pdf",
                                 "测试工程师简历.docx", "Java高级开发简历.pdf", "全栈工程师简历.docx",
                                 "后端开发简历.pdf", "技术经理简历.docx"};

            for (int i = 0; i < users.size() && i < fileNames.length; i++) {
                Resume resume = new Resume();
                resume.setUserId(users.get(i).getId());
                resume.setFileName(fileNames[i]);
                resume.setFileUrl("/uploads/resume_" + (i + 1) + ".pdf");
                resume.setFileSize(1024L * (100 + i * 50));
                resume.setParsedText("候选人具有 " + (3 + i) + " 年工作经验，熟悉 Java/Spring Boot 技术栈...");
                resume.setStructuredData(Map.of(
                    "name", users.get(i).getNickname(),
                    "experience", (3 + i) + "年",
                    "skills", java.util.Arrays.asList("Java", "Spring Boot", "MySQL")
                ));
                resume.setIsActive(true);
                resumeRepository.save(resume);
            }
            log.info("初始化 {} 份简历", users.size());
        }
    }

    private void initKnowledgeDocs() {
        if (knowledgeDocRepository.count() == 0) {
            KnowledgeDoc doc1 = new KnowledgeDoc();
            doc1.setTitle("Java 集合框架详解");
            doc1.setContent("Java 集合框架主要包括 Collection 和 Map 两大接口...\n\nList 接口：\n- ArrayList：基于动态数组\n- LinkedList：基于双向链表\n\nSet 接口：\n- HashSet：基于哈希表\n- TreeSet：基于红黑树\n\nMap 接口：\n- HashMap：基于哈希表\n- TreeMap：基于红黑树");
            doc1.setCategory("java");
            doc1.setTags(new String[]{"集合", "Java基础", "数据结构"});
            doc1.setIsActive(true);

            KnowledgeDoc doc2 = new KnowledgeDoc();
            doc2.setTitle("Spring Boot 核心概念");
            doc2.setContent("Spring Boot 是 Spring 的快速开发框架...\n\n核心特性：\n1. 自动配置\n2. 起步依赖\n3. 内嵌服务器\n4. 生产就绪特性\n\n常用注解：\n- @SpringBootApplication\n- @RestController\n- @Service\n- @Repository\n- @Autowired");
            doc2.setCategory("java");
            doc2.setTags(new String[]{"Spring Boot", "框架", "自动配置"});
            doc2.setIsActive(true);

            KnowledgeDoc doc3 = new KnowledgeDoc();
            doc3.setTitle("计算机网络基础");
            doc3.setContent("OSI 七层模型：\n1. 物理层\n2. 数据链路层\n3. 网络层\n4. 传输层\n5. 会话层\n6. 表示层\n7. 应用层\n\nTCP/IP 四层模型：\n1. 网络接口层\n2. 网络层\n3. 传输层\n4. 应用层");
            doc3.setCategory("computer_basics");
            doc3.setTags(new String[]{"网络", "TCP/IP", "OSI"});
            doc3.setIsActive(true);

            KnowledgeDoc doc4 = new KnowledgeDoc();
            doc4.setTitle("MySQL 索引优化");
            doc4.setContent("索引类型：\n1. 主键索引\n2. 唯一索引\n3. 普通索引\n4. 组合索引\n\n优化原则：\n- 最左前缀原则\n- 避免索引失效\n- 覆盖索引\n- 索引下推");
            doc4.setCategory("database");
            doc4.setTags(new String[]{"MySQL", "索引", "优化"});
            doc4.setIsActive(true);

            KnowledgeDoc doc5 = new KnowledgeDoc();
            doc5.setTitle("项目经验 - 电商平台");
            doc5.setContent("项目名称：B2C 电商平台\n\n技术栈：\n- 后端：Spring Boot + MyBatis\n- 前端：Vue.js + Element UI\n- 数据库：MySQL + Redis\n- 消息队列：RabbitMQ\n\n核心功能：\n1. 用户管理\n2. 商品管理\n3. 订单管理\n4. 支付集成\n5. 库存管理");
            doc5.setCategory("project");
            doc5.setTags(new String[]{"电商", "项目经验", "Spring Boot"});
            doc5.setIsActive(true);

            KnowledgeDoc doc6 = new KnowledgeDoc();
            doc6.setTitle("Java 多线程与并发编程");
            doc6.setContent("线程创建方式：\n1. 继承 Thread 类\n2. 实现 Runnable 接口\n3. 实现 Callable 接口\n\n线程池：\n- FixedThreadPool：固定大小\n- CachedThreadPool：缓存\n- ScheduledThreadPool：定时任务\n- SingleThreadExecutor：单线程\n\n并发工具：\n- CountDownLatch：等待多个线程完成\n- CyclicBarrier：线程间同步\n- Semaphore：控制并发访问数\n- ConcurrentHashMap：线程安全Map");
            doc6.setCategory("java");
            doc6.setTags(new String[]{"多线程", "并发", "线程池"});
            doc6.setIsActive(true);

            KnowledgeDoc doc7 = new KnowledgeDoc();
            doc7.setTitle("Redis 缓存与分布式锁");
            doc7.setContent("Redis 数据类型：\n1. String：字符串\n2. Hash：哈希\n3. List：列表\n4. Set：集合\n5. ZSet：有序集合\n\n缓存策略：\n- 缓存穿透：布隆过滤器\n- 缓存击穿：互斥锁\n- 缓存雪崩：过期时间随机化\n\n分布式锁实现：\n- SETNX + 过期时间\n- Redisson 框架");
            doc7.setCategory("database");
            doc7.setTags(new String[]{"Redis", "缓存", "分布式锁"});
            doc7.setIsActive(true);

            KnowledgeDoc doc8 = new KnowledgeDoc();
            doc8.setTitle("Vue.js 核心知识点");
            doc8.setContent("Vue 3 新特性：\n1. Composition API\n2. 响应式系统重构（Proxy）\n3. 更好的 TypeScript 支持\n4. Teleport 组件\n5. Suspense 组件\n\n常用指令：\n- v-bind / v-model\n- v-if / v-show\n- v-for\n- v-on\n\n生命周期：\n- onMounted\n- onUpdated\n- onUnmounted");
            doc8.setCategory("computer_basics");
            doc8.setTags(new String[]{"Vue.js", "前端", "JavaScript"});
            doc8.setIsActive(true);

            KnowledgeDoc doc9 = new KnowledgeDoc();
            doc9.setTitle("微服务架构设计");
            doc9.setContent("微服务核心概念：\n1. 服务拆分原则\n2. 服务注册与发现（Nacos/Eureka）\n3. 配置中心\n4. 服务网关（Gateway）\n5. 负载均衡\n\n通信方式：\n- 同步：REST、gRPC\n- 异步：消息队列（RabbitMQ/Kafka）\n\n分布式事务：\n- Seata\n- 最终一致性\n- Saga 模式");
            doc9.setCategory("java");
            doc9.setTags(new String[]{"微服务", "架构", "分布式"});
            doc9.setIsActive(true);

            KnowledgeDoc doc10 = new KnowledgeDoc();
            doc10.setTitle("Git 版本控制最佳实践");
            doc10.setContent("Git 工作流：\n1. Git Flow：master/develop/feature/release/hotfix\n2. GitHub Flow：简化版\n3. Trunk-Based：主干开发\n\n常用命令：\n- git commit -m \"feat/fix/docs/style/refactor/test/chore: 描述\"\n- git rebase -i（交互式变基）\n- git stash（暂存修改）\n- git cherry-pick（挑选提交）\n\n分支策略：\n- 功能分支从 develop 创建\n- 修复分支从 master 创建\n- 合并前必须 Code Review");
            doc10.setCategory("computer_basics");
            doc10.setTags(new String[]{"Git", "版本控制", "协作"});
            doc10.setIsActive(true);

            knowledgeDocRepository.saveAll(List.of(doc1, doc2, doc3, doc4, doc5, doc6, doc7, doc8, doc9, doc10));
            log.info("初始化 10 个知识文档");
        }
    }

    private void initJobPositions() {
        if (jobPositionRepository.count() == 0) {
            JobPosition p1 = new JobPosition();
            p1.setName("Java开发工程师");
            p1.setCategory("技术研发");
            p1.setDescription("负责公司核心业务系统的后端开发，参与系统架构设计");
            p1.setRequirements("1. 本科及以上学历，计算机相关专业\n2. 3年以上Java开发经验\n3. 熟悉Spring Boot、MyBatis等框架\n4. 熟悉MySQL数据库，了解Redis\n5. 良好的沟通能力和团队协作精神");
            p1.setInterviewQuestions("1. 请介绍Java集合框架\n2. Spring Boot自动配置原理\n3. MySQL索引优化\n4. Redis使用场景\n5. 微服务架构理解");
            p1.setSort(1);
            p1.setIsActive(true);

            JobPosition p2 = new JobPosition();
            p2.setName("前端开发工程师");
            p2.setCategory("技术研发");
            p2.setDescription("负责公司Web端和移动端前端开发，优化用户体验");
            p2.setRequirements("1. 本科及以上学历，计算机相关专业\n2. 2年以上前端开发经验\n3. 熟练掌握Vue.js或React\n4. 熟悉HTML5、CSS3、JavaScript\n5. 了解Webpack等构建工具");
            p2.setInterviewQuestions("1. Vue.js响应式原理\n2. React Hooks使用\n3. CSS布局方式\n4. JavaScript闭包\n5. 前端性能优化");
            p2.setSort(2);
            p2.setIsActive(true);

            JobPosition p3 = new JobPosition();
            p3.setName("后端开发工程师");
            p3.setCategory("技术研发");
            p3.setDescription("负责服务端API开发，数据库设计，系统性能优化");
            p3.setRequirements("1. 本科及以上学历\n2. 熟悉Java/Python/Go任一语言\n3. 熟悉RESTful API设计\n4. 熟悉数据库设计和优化\n5. 了解分布式系统");
            p3.setInterviewQuestions("1. RESTful API设计原则\n2. 数据库事务隔离级别\n3. 分布式锁实现\n4. 消息队列使用场景\n5. 系统架构设计");
            p3.setSort(3);
            p3.setIsActive(true);

            JobPosition p4 = new JobPosition();
            p4.setName("全栈工程师");
            p4.setCategory("技术研发");
            p4.setDescription("负责前后端开发，独立完成功能模块");
            p4.setRequirements("1. 本科及以上学历\n2. 3年以上全栈开发经验\n3. 熟悉前端框架和后端框架\n4. 熟悉数据库和服务器部署\n5. 独立完成项目能力");
            p4.setInterviewQuestions("1. 前后端分离架构\n2. JWT认证机制\n3. 文件上传方案\n4. 数据库设计\n5. 部署运维经验");
            p4.setSort(4);
            p4.setIsActive(true);

            JobPosition p5 = new JobPosition();
            p5.setName("产品经理");
            p5.setCategory("产品");
            p5.setDescription("负责产品规划、需求分析、项目管理");
            p5.setRequirements("1. 本科及以上学历\n2. 2年以上产品经理经验\n3. 熟悉Axure、Figma等工具\n4. 良好的沟通和协调能力\n5. 数据分析能力");
            p5.setInterviewQuestions("1. 需求分析方法\n2. 产品路线图规划\n3. 用户故事编写\n4. 数据驱动决策\n5. 项目管理经验");
            p5.setSort(5);
            p5.setIsActive(true);

            JobPosition p6 = new JobPosition();
            p6.setName("测试工程师");
            p6.setCategory("质量保障");
            p6.setDescription("负责软件质量保证，编写测试用例，执行测试");
            p6.setRequirements("1. 本科及以上学历\n2. 2年以上测试经验\n3. 熟悉测试方法和工具\n4. 了解自动化测试\n5. 细心、责任心强");
            p6.setInterviewQuestions("1. 测试用例设计方法\n2. 自动化测试框架\n3. Bug管理流程\n4. 性能测试方法\n5. 持续集成");
            p6.setSort(6);
            p6.setIsActive(true);

            JobPosition p7 = new JobPosition();
            p7.setName("UI设计师");
            p7.setCategory("设计");
            p7.setDescription("负责产品界面设计，用户体验优化");
            p7.setRequirements("1. 设计相关专业\n2. 熟练使用Figma、Sketch\n3. 良好的审美能力\n4. 了解前端基础\n5. 作品集");
            p7.setInterviewQuestions("1. 设计原则\n2. 色彩理论\n3. 响应式设计\n4. 用户体验优化\n5. 设计系统");
            p7.setSort(7);
            p7.setIsActive(true);

            JobPosition p8 = new JobPosition();
            p8.setName("运维工程师");
            p8.setCategory("运维");
            p8.setDescription("负责服务器运维，系统监控，故障处理");
            p8.setRequirements("1. 熟悉Linux系统\n2. 熟悉Docker、K8s\n3. 熟悉CI/CD流程\n4. 了解云平台\n5. 故障排查能力");
            p8.setInterviewQuestions("1. Linux常用命令\n2. Docker容器化\n3. K8s集群管理\n4. 监控告警\n5. 故障排查");
            p8.setSort(8);
            p8.setIsActive(true);

            jobPositionRepository.save(p1);
            jobPositionRepository.save(p2);
            jobPositionRepository.save(p3);
            jobPositionRepository.save(p4);
            jobPositionRepository.save(p5);
            jobPositionRepository.save(p6);
            jobPositionRepository.save(p7);
            jobPositionRepository.save(p8);
            log.info("初始化 8 个岗位");
        }
    }

    private void initSessions() {
        if (sessionRepository.count() == 0) {
            var users = userRepository.findAll();
            var resumes = resumeRepository.findAll();
            String[] positions = {"Java开发工程师", "前端开发工程师", "产品经理", "测试工程师",
                                 "Java高级开发", "全栈工程师", "后端开发", "技术经理"};
            String[] difficulties = {"easy", "medium", "hard"};
            String[] statuses = {"completed", "ongoing"};

            for (int i = 0; i < users.size(); i++) {
                InterviewSession session = new InterviewSession();
                session.setUserId(users.get(i).getId());
                session.setResumeId(resumes.get(i).getId());
                session.setJobPosition(positions[i % positions.length]);
                session.setDifficulty(difficulties[i % difficulties.length]);
                session.setStatus(statuses[i % statuses.length]);
                session.setQuestionCount(5 + (i % 5));
                session.setStartTime(LocalDateTime.now().minusDays(i));
                if ("completed".equals(session.getStatus())) {
                    session.setEndTime(LocalDateTime.now().minusDays(i).plusHours(1));
                }
                sessionRepository.save(session);

                // 创建面试消息
                for (int j = 0; j < session.getQuestionCount(); j++) {
                    InterviewMessage question = new InterviewMessage();
                    question.setSessionId(session.getId());
                    question.setRole("assistant");
                    question.setContent("请介绍一下你在" + positions[i % positions.length] + "岗位上的项目经验？");
                    question.setIsQuestion(true);
                    messageRepository.save(question);

                    InterviewMessage answer = new InterviewMessage();
                    answer.setSessionId(session.getId());
                    answer.setRole("user");
                    answer.setContent("我在上一个项目中负责了核心模块的开发，使用了Spring Boot和MySQL...");
                    answer.setIsQuestion(false);
                    messageRepository.save(answer);
                }

                // 创建评估报告（仅已完成）
                if ("completed".equals(session.getStatus())) {
                    AssessmentReport report = new AssessmentReport();
                    report.setSessionId(session.getId());
                    report.setOverallScore(75.0 + (i * 2.5));
                    report.setScoreLogic(70.0 + (i * 3));
                    report.setScoreDepth(72.0 + (i * 2));
                    report.setScoreMatch(78.0 + (i * 1.5));
                    report.setScoreCommunication(76.0 + (i * 2.5));
                    report.setImprovementSuggestions("建议加强算法和数据结构的学习，多参与开源项目...");
                    report.setDetailedFeedback(Map.of(
                        "strengths", java.util.Arrays.asList("项目经验丰富", "技术栈匹配"),
                        "weaknesses", java.util.Arrays.asList("算法基础薄弱", "缺乏系统设计经验")
                    ));
                    report.setGenerateStatus("completed");
                    reportRepository.save(report);
                }
            }
            log.info("初始化 {} 个面试会话", users.size());
        }
    }
}
