require 'rake/clean'

# Configuration
NAME = "report"

TEX_EXEC = "xelatex"

# Semi-Configuration
PDF_NAME = "#{NAME}.pdf"
TEX_NAME = "#{NAME}.tex"
DEFAULT_ARGS = "-shell-escape -interaction=nonstopmode -papersize=a4"

# Cleaning FileLists
CLEAN.include(['**/*.aux', '*.log', '*.out', '*.pyg', '*.bak', '*.toc', '*.bbl', '*.blg', '*.toc'])
CLOBBER.include('*.pdf')

task :default => [:build, :clean]

desc "Build the document"
task :build do
    puts "Building the PDF... [#{TEX_NAME} => #{PDF_NAME}]"
    latex DEFAULT_ARGS, TEX_NAME
    `bibtex #{NAME}`
    latex DEFAULT_ARGS, TEX_NAME
    latex DEFAULT_ARGS, TEX_NAME
end

desc "Show the document"
task :view => [PDF_NAME] do
  puts "Opening the PDF... [#{PDF_NAME}]"
  ["open", "okular", "kpdf", "acroread", "start"].find do |viewer|
    command viewer, PDF_NAME
  end or
  puts "Unable to find any pdf viewer."
end

# Load in the other tasks.
Dir.glob("tools/tasks/*.rake").each { |r| load r }
