%%% cs4rep.cls
%%% Style for CS4 projects
%%% AJCD 31/8/90
%%% Last updated 9/10/90
%%% JTB 14/12/94 - modified for LaTeX2e
%%% JTB 02/02/95 - modified for stg
%%% Roni Khardon 8/10/99 - modified for Informatics Degrees
%%% Roni Khardon 24/11/99 - modified (single spaced absract)
%%% Mary Ellen Foster 17/3/2000 - change use of parskip.clo to parskip.sty




% ----------------------------------
%    Identification   
% ----------------------------------

\NeedsTeXFormat{LaTeX2e}[1994/06/01]
\ProvidesClass{cs4rep}[1995/02/02 DCS CS4 Report class]

% ----------------------------------
%    Initial code   
% ----------------------------------

\newcommand{\clearemptydoublepage}{\newpage{\pagestyle{empty}\cleardoublepage}}

%%% Set up some standard variables
\def\deptname#1{\gdef\@deptname{#1}}
\gdef\@deptname{Division of Informatics}

% set degree and report type in tex source
\def\degree#1{\gdef\@degree{#1}} 
\def\project#1{\gdef\@project{#1}}
%\newcommand\@project{4th Year Project Report}


\newcommand{\optionerror}[1]{\typeout{*** Warning: #1 is not a legal class
option}}

% This gets set to true if the parskip option is specified
\newif\if@cparskip \@cparskipfalse

% ----------------------------------
%    Declaration of options   
% ----------------------------------


\DeclareOption{a5paper}{\OptionNotUsed\optionerror{a5paper}}
\DeclareOption{b5paper}{\OptionNotUsed\optionerror{b5paper}}
\DeclareOption{letterpaper}{\OptionNotUsed\optionerror{letterpaper}}
\DeclareOption{legalpaper}{\OptionNotUsed\optionerror{legalpaper}}
\DeclareOption{executivepaper}{\OptionNotUsed\optionerror{executivepaper}}
\DeclareOption{landscape}{\OptionNotUsed\optionerror{landscape}}
\DeclareOption{10pt}{\OptionNotUsed\optionerror{10pt}}
\DeclareOption{11pt}{\OptionNotUsed\optionerror{11pt}}
\DeclareOption{oneside}{\OptionNotUsed\optionerror{oneside}}
\DeclareOption{draft}{\OptionNotUsed\optionerror{draft}}
\DeclareOption{notitlepage}{\OptionNotUsed\optionerror{notitlepage}}
\DeclareOption{openany}{\OptionNotUsed\optionerror{openany}}
\DeclareOption{onecolumn}{\OptionNotUsed\optionerror{onecolumn}}
\DeclareOption{twocolumn}{\OptionNotUsed\optionerror{twocolum}}
\DeclareOption{parskip}{\typeout{Parskip option}\@cparskiptrue}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{report}}

% ----------------------------------
%    Execution of options   
% ----------------------------------

\ProcessOptions

% ----------------------------------
%    Package Loading   
% ----------------------------------

\LoadClass[a4paper,twoside,openright,12pt,titlepage]{report}[1994/06/02]
\RequirePackage{a4}
% Only load parskip if they said to...
\if@cparskip
  \typeout{Parskip package loaded}
  \RequirePackage{parskip}
\else\fi
%\RequirePackage{a4wide}
%\RequirePackage{doublespace}

% ----------------------------------
%    Main code
% ----------------------------------


%%% Table of contents to list down to subsections and no further
\setcounter{tocdepth}{2}

%%% Number down to subsubsections only
\setcounter{secnumdepth}{3}

%%% Get rid of Chapter titles
\renewcommand\chaptername{}

%%% Change the space before Chapter headings - ripped out of report.cls
\def\@makechapterhead#1{%
%  \vspace*{50\p@}%
  {\parindent \z@ \raggedright \reset@font
    \ifnum \c@secnumdepth >\m@ne
         \huge\bfseries \@chapapp{} \thechapter.\ \
%         \par
%         \vskip 20\p@
       \fi
%    \Huge \bfseries 
    #1\par
    \nobreak
%    \vskip 40\p@
    \vskip 30\p@
  }}

\def\@makeschapterhead#1{%
%  \vspace*{50\p@}%
  {\parindent \z@ \raggedright
    \reset@font
%    \Huge \bfseries  
    \huge \bfseries  
    #1\par
    \nobreak
%    \vskip 40\p@
    \vskip 30\p@
  }}

%%% Get rid of the original abstract environment and replace it with
%%% our version so that we can put it at the end of the titlepage
%%% instead of on the first page by itself
%\def\abstract#1{\gdef\@abstract{#1}} 
\def\abstract#1{\gdef\@abstract{
%     \begin{singlespace} 
     #1 
%     \end{singlespace}
     }} 
\let\@abstract\relax
\def\abstractname{Abstract: }
\let\frametitle\relax

%%% Purple covers are obsolete?
%\def\orangecover{%
%  \def\windowtop{85mm}\def\windowleft{51mm}%
%  \def\windowheight{68mm}\def\windowwidth{108mm}}

\def\orangecover{%
  \def\windowtop{62mm}\def\windowleft{55mm}%
  \def\windowheight{68mm}\def\windowwidth{108mm}}

\def\purplecover{%
  \def\windowtop{72mm}\def\windowleft{66mm}%
  \def\windowheight{62mm}\def\windowwidth{88mm}}

\orangecover

%%% Lots of magic to make the titlepage fit into the orange covers and
%%% put the abstract on the titlepage

%%% Hack the titlepage from report.cls to stop it producing an extra
%%% first page in place of the usual abstract page

\renewenvironment{titlepage}
    {%
      \if@twocolumn
        \@restonecoltrue\onecolumn
      \else
%        \@restonecolfalse\newpage
        \@restonecolfalse
      \fi
      \thispagestyle{empty}%
      \if@compatibility
        \setcounter{page}{0}
      \else
        \setcounter{page}{1}%
      \fi}%
    {%\if@restonecol\twocolumn \else \newpage \fi
      \if@restonecol\twocolumn \else \fi
%      \setcounter{page}{1}%
    }

\renewcommand\maketitle{\begin{titlepage}
%\begin{spacing}{1.5}
\let\footnotesize\small
\let\footnoterule\relax
%\setcounter{page}{0}
\null\vfil
\vskip -1in % get rid of default driver margin
\vskip -\voffset \vskip -\topmargin \vskip -\headheight  % get rid of
\vskip -\headsep \vskip -\headheight \vskip -\topskip    % TeX margins
\vbox to \windowtop{% window offset from top of page
\Huge \vss\begin{center}%
University of Edinburgh\\
\@deptname
\end{center}\vss}%
\hbox{\hskip -1in % get rid of default driver margin
\hskip -\hoffset \hskip -\evensidemargin  % get rid of TeX margins
\hskip \windowleft % window offset from left side of page
\parbox{\windowwidth}{% box of window width
\frametitle{\vbox to \windowheight{% box of window height
\vss  % centre vertically
\begin{center}  % center horizontally
  {\large\rm \@title \par} \vskip 1em plus 1fil
  {\large \@project} \\ % \par} \vfil
  {\large \@degree \par} \vfil
  {\normalsize\rm \lineskip .75em
   \begin{tabular}[t]{c}%
      \@author
   \end{tabular}\par} \vfil
  {\normalsize \@date \par}
\end{center}%
\vss}}}} \par
\ifx\@abstract\relax\typeout{*** Warning: CS4 report regulations require an abstract.}\else
  \null\vfil
%  \begin{center}\bf\abstractname\end{center}
   {\bf \abstractname}
%  \begin{singlespace} \@abstract \end{singlespace}
  \@abstract
\fi
\null\vfil\@thanks
\null\par
%\end{spacing}
\end{titlepage}
\setcounter{footnote}{0}
\if@openright
  \clearemptydoublepage
\else
  \clearpage
\fi
\let\thanks\relax
\gdef\@thanks{}\gdef\@author{}\gdef\@title{}\gdef\@abstract{}\gdef\@project{}%
\let\maketitle\relax
\let\abstract\relax}

%%% Front matter has roman numerals - toc redefinition below changes
%%% the pagenumbering style for the main matter
\AtBeginDocument{\pagenumbering{roman}\pagestyle{empty}}

%%% Run the toc tighter - nicked out of ltxguide.cls

\def\@starttoc#1{\begingroup
  \makeatletter
  \parskip\z@\@plus\p@\relax         % <----
  \@input{\jobname.#1}\if@filesw 
  \expandafter\newwrite\csname tf@#1\endcsname
             \immediate\openout
                 \csname tf@#1\endcsname \jobname.#1\relax
  \fi \global\@nobreakfalse \endgroup}
 
%%% Put blank pages at end of toc and reset pagenumbering and style
\renewcommand\tableofcontents{%
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \chapter*{\contentsname
        \@mkboth{\uppercase{\contentsname}}{\uppercase{\contentsname}}}%
    \@starttoc{toc}%
    \if@restonecol\twocolumn\fi
    \if@openright
       \clearemptydoublepage
    \else
       \clearpage
    \fi
    \pagestyle{headings}
    \pagenumbering{arabic}
    }

%%% Get rid of \listoftables and \listoffigures
\renewcommand\listoftables{%
    \typeout{*** List of tables removed: CS4 reports don't need a list of tables}
    }

\renewcommand\listoffigures{%
    \typeout{*** List of figures removed: CS4 reports don't need a list of figures}
    }

%%% spacing for rest of report
%\setstretch{1.5}
%\setstretch{1.0}
%\normalbaselineskip\baselineskip

\renewenvironment{thebibliography}[1]
     {\chapter*{\bibname
        \@mkboth{\MakeUppercase\bibname}{\MakeUppercase\bibname}}%
  \addcontentsline{toc}{chapter}{Bibliography}
      \list{\@biblabel{\@arabic\c@enumiv}}%
           {\settowidth\labelwidth{\@biblabel{#1}}%
            \leftmargin\labelwidth
            \advance\leftmargin\labelsep
            \@openbib@code
            \usecounter{enumiv}%
            \let\p@enumiv\@empty
            \renewcommand\theenumiv{\@arabic\c@enumiv}}%
      \sloppy
      \clubpenalty4000
      \@clubpenalty \clubpenalty
      \widowpenalty4000%
      \sfcode`\.\@m}
     {\def\@noitemerr
       {\@latex@warning{Empty `thebibliography' environment}}%
      \endlist}



