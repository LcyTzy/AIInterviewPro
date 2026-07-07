-- ============================================================
-- 0. 删除所有已存在的表（重建）
-- ============================================================
DROP TABLE IF EXISTS admin_audit_logs CASCADE;
DROP TABLE IF EXISTS system_configs CASCADE;
DROP TABLE IF EXISTS knowledge_docs CASCADE;
DROP TABLE IF EXISTS assessment_reports CASCADE;
DROP TABLE IF EXISTS interview_messages CASCADE;
DROP TABLE IF EXISTS interview_sessions CASCADE;
DROP TABLE IF EXISTS resumes CASCADE;
DROP TABLE IF EXISTS admin_users CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================================
-- 1. 启用 pgvector 扩展（如果尚未安装，先注释掉）
-- ============================================================
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- 2. 用户表（C端）
-- ============================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    openid VARCHAR(64) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    nickname VARCHAR(50) NOT NULL DEFAULT '求职者',
    avatar_url VARCHAR(500),
    gender SMALLINT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. 管理员表（后台）
-- ============================================================
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    avatar VARCHAR(500),
    role VARCHAR(20) DEFAULT 'OPERATOR',
    is_active BOOLEAN DEFAULT TRUE,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO admin_users (username, password, name, role) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', '超级管理员', 'SUPER')
ON CONFLICT (username) DO NOTHING;

-- ============================================================
-- 4. 简历表（含向量字段）
-- ============================================================
CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    parsed_text TEXT,
    structured_data JSONB,
    embedding VECTOR(1536),
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_resume_user ON resumes(user_id);
CREATE INDEX idx_resume_struct ON resumes USING GIN (structured_data);
CREATE INDEX idx_resume_embedding ON resumes USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- ============================================================
-- 5. 面试会话表
-- ============================================================
CREATE TABLE interview_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    resume_id BIGINT REFERENCES resumes(id),
    job_position VARCHAR(100) NOT NULL,
    difficulty VARCHAR(20) DEFAULT 'medium',
    status VARCHAR(20) DEFAULT 'ongoing',
    question_count INT DEFAULT 0,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP
);
CREATE INDEX idx_session_user ON interview_sessions(user_id);
CREATE INDEX idx_session_status ON interview_sessions(status);

-- ============================================================
-- 6. 对话消息表
-- ============================================================
CREATE TABLE interview_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES interview_sessions(id) ON DELETE CASCADE,
    role VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    is_question BOOLEAN DEFAULT FALSE,
    tokens_used INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_msg_session ON interview_messages(session_id);
CREATE INDEX idx_msg_time ON interview_messages(create_time);

-- ============================================================
-- 7. 评估报告表
-- ============================================================
CREATE TABLE assessment_reports (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT UNIQUE NOT NULL REFERENCES interview_sessions(id) ON DELETE CASCADE,
    overall_score DECIMAL(5,2),
    score_logic DECIMAL(5,2),
    score_depth DECIMAL(5,2),
    score_match DECIMAL(5,2),
    score_communication DECIMAL(5,2),
    improvement_suggestions TEXT,
    detailed_feedback JSONB,
    generate_status VARCHAR(20) DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_report_session ON assessment_reports(session_id);
CREATE INDEX idx_report_status ON assessment_reports(generate_status);

-- ============================================================
-- 8. 知识库文档表（RAG）
-- ============================================================
CREATE TABLE knowledge_docs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50),
    tags VARCHAR(255)[],
    embedding VECTOR(1536),
    metadata JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_knowledge_category ON knowledge_docs(category);
CREATE INDEX idx_knowledge_tags ON knowledge_docs USING GIN(tags);
CREATE INDEX idx_knowledge_embedding ON knowledge_docs USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- ============================================================
-- 9. 系统配置表
-- ============================================================
CREATE TABLE system_configs (
    id SMALLSERIAL PRIMARY KEY,
    config_key VARCHAR(50) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(200),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO system_configs (config_key, config_value, description) VALUES
('ai_model', 'deepseek-chat', '当前使用的大模型'),
('max_question_rounds', '10', '单次面试最大问答轮次')
ON CONFLICT (config_key) DO NOTHING;

-- ============================================================
-- 10. 操作日志表
-- ============================================================
CREATE TABLE admin_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_user VARCHAR(50),
    action VARCHAR(50),
    target_id BIGINT,
    detail JSONB,
    ip VARCHAR(45),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);