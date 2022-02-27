USE [Camunda]
GO

/****** Object:  Table [dbo].[workflowLogs]    Script Date: 1/12/2563 16:59:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[workflowLogs](
       [transactionId] [varchar](50) NOT NULL,
       [policyNumber] [varchar](50) NOT NULL,
       [partnerName] [varchar](50) NOT NULL,
       [stepName] [varchar](50) NOT NULL,
       [resultStatus] [varchar](50) NOT NULL,
       [errorDescription] [varchar](250) NOT NULL,
       [jsonData] [varchar](1000) NULL,
       [createdDate] [datetime] NOT NULL,
       [createdBy] [varchar](50) NOT NULL,
       [updatedDate] [datetime] NULL,
       [updatedBy] [varchar](50) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO