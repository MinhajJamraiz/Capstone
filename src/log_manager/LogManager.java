package application;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogManager {
    private final Path baseDir;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Writers map for character streams
    private final Map<String, BufferedWriter> writers = new HashMap<>();
    // binary streams map for simulation of bytes
    private final Map<String, FileOutputStream> binStreams = new HashMap<>();

    // file name regex helpers
    // Example filenames: SYSTEM_2025-10-26.log, ROBOT-R-1_2025-10-26.log, CS-CS-1_2025-10-26.log
    private final Pattern logFilePattern = Pattern.compile("^(?<target>[A-Za-z0-9\\-]+(?:_[A-Za-z0-9\\-]+)*)_(?<date>\\d{4}-\\d{2}-\\d{2})\\.(?<ext>log|bin)$");

    public LogManager(Path baseDir) {
        this.baseDir = baseDir;
        try {
            if (!Files.exists(baseDir)) Files.createDirectories(baseDir);
            if (!Files.exists(baseDir.resolve("archive"))) Files.createDirectories(baseDir.resolve("archive"));
            if (!Files.exists(baseDir.resolve("moved"))) Files.createDirectories(baseDir.resolve("moved"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log directories: " + e.getMessage(), e);
        }
        logSystem("LogManager initialized at " + baseDir.toAbsolutePath());
    }

    private synchronized String todayStr() {
        return LocalDate.now().format(dateFmt);
    }

    private synchronized Path charLogPath(String target, String date) {
        String fname = target + "_" + date + ".log";
        return baseDir.resolve(fname);
    }

    private synchronized Path binLogPath(String target, String date) {
        String fname = target + "_" + date + ".bin";
        return baseDir.resolve(fname);
    }

    private synchronized BufferedWriter getWriter(String target, String date) throws IOException {
        String key = target + "_" + date + ".log";
        if (writers.containsKey(key)) return writers.get(key);
        Path p = charLogPath(target, date);
        BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        writers.put(key, bw);
        return bw;
    }

    private synchronized FileOutputStream getBinStream(String target, String date) throws IOException {
        String key = target + "_" + date + ".bin";
        if (binStreams.containsKey(key)) return binStreams.get(key);
        Path p = binLogPath(target, date);
        FileOutputStream fos = new FileOutputStream(p.toFile(), true); // append
        binStreams.put(key, fos);
        return fos;
    }

    // generic low-level logging method — writes both char and byte streams
    private synchronized void writeLog(String target, String message, String date) {
        String ts = sdf.format(new Date());
        String line = String.format("%s %s%n", ts, message);
        try {
            BufferedWriter bw = getWriter(target, date);
            bw.write(line);
            bw.flush();
        } catch (IOException ioe) {
            System.err.println("Failed to write char log for " + target + ": " + ioe.getMessage());
        }
        // also write raw bytes
        try {
            FileOutputStream fos = getBinStream(target, date);
            byte[] data = line.getBytes(StandardCharsets.UTF_8);
            fos.write(data);
            fos.flush();
        } catch (IOException ioe) {
            System.err.println("Failed to write byte log for " + target + ": " + ioe.getMessage());
        }
    }

    // high-level helpers
    public void logSystem(String message) {
        writeLog("SYSTEM", message, todayStr());
    }
    public void logSystemWarning(String message) { writeLog("SYSTEM", "WARN: " + message, todayStr()); }
    public void logSystemSevere(String message) { writeLog("SYSTEM", "SEVERE: " + message, todayStr()); }

    public void logRobot(String robotId, String message) {
        writeLog("ROBOT-" + robotId, message, todayStr());
    }

    public void logChargingStation(String csId, String message) {
        writeLog("CS-" + csId, message, todayStr());
    }

    public void logStorage(String message) {
        writeLog("STORAGE", message, todayStr());
    }
    public void logStorageWarning(String message) { writeLog("STORAGE", "WARN: " + message, todayStr()); }
    public void logStorageSevere(String message) { writeLog("STORAGE", "SEVERE: " + message, todayStr()); }

    // list log files optionally filtered by regex
    public synchronized List<Path> listLogs(String regexFilter) {
        List<Path> out = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(baseDir)) {
            Pattern p = null;
            if (regexFilter != null && !regexFilter.isEmpty()) {
                p = Pattern.compile(regexFilter, Pattern.CASE_INSENSITIVE);
            }
            for (Path pth : ds) {
                if (Files.isRegularFile(pth)) {
                    String fname = pth.getFileName().toString();
                    if (fname.equals("archive") || fname.equals("moved")) continue;
                    if (p == null || p.matcher(fname).find()) out.add(pth);
                }
            }
        } catch (IOException e) {
            logSystemSevere("Error listing logs: " + e.getMessage());
        }
        return out;
    }

    // open a log file by equipment or date: equipmentName can be "ROBOT-R-1" or "SYSTEM" etc.
    // dateFormat must be yyyy-MM-dd or if null we open today's
    public synchronized Optional<String> readLog(String equipmentTargetOrFilename, String dateStr) {
        // First allow user to pass either equipment target or exact filename
        String date = (dateStr == null || dateStr.isEmpty()) ? todayStr() : dateStr;
        // If they passed full filename
        Path candidate = baseDir.resolve(equipmentTargetOrFilename);
        if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
            // detect whether it's text log or bin; for bin we'll show hex preview
            try {
                if (candidate.toString().endsWith(".log")) {
                    return Optional.of(new String(Files.readAllBytes(candidate), StandardCharsets.UTF_8));
                } else {
                    byte[] bytes = Files.readAllBytes(candidate);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Binary file preview (").append(bytes.length).append(" bytes)\n");
                    for (int i = 0; i < Math.min(bytes.length, 1024); i++) {
                        sb.append(String.format("%02X ", bytes[i]));
                        if ((i + 1) % 16 == 0) sb.append('\n');
                    }
                    return Optional.of(sb.toString());
                }
            } catch (IOException e) {
                logSystemSevere("Failed to read log file " + candidate + ": " + e.getMessage());
                return Optional.empty();
            }
        }

        // else assume target + date
        Path text = charLogPath(equipmentTargetOrFilename, date);
        if (Files.exists(text)) {
            try {
                return Optional.of(new String(Files.readAllBytes(text), StandardCharsets.UTF_8));
            } catch (IOException e) {
                logSystemSevere("Failed to read log: " + e.getMessage());
                return Optional.empty();
            }
        }
        Path bin = binLogPath(equipmentTargetOrFilename, date);
        if (Files.exists(bin)) {
            try {
                byte[] bytes = Files.readAllBytes(bin);
                StringBuilder sb = new StringBuilder();
                sb.append("Binary file preview (").append(bytes.length).append(" bytes)\n");
                for (int i = 0; i < Math.min(bytes.length, 1024); i++) {
                    sb.append(String.format("%02X ", bytes[i]));
                    if ((i + 1) % 16 == 0) sb.append('\n');
                }
                return Optional.of(sb.toString());
            } catch (IOException e) {
                logSystemSevere("Failed to read binary log: " + e.getMessage());
                return Optional.empty();
            }
        }
        // fallback: try to search by regex (equipment substring)
        List<Path> hits = listLogs("(?i)" + Pattern.quote(equipmentTargetOrFilename) + ".*" + date.replace("-", "\\-") + ".*");
        if (!hits.isEmpty()) {
            try {
                return Optional.of(new String(Files.readAllBytes(hits.get(0)), StandardCharsets.UTF_8));
            } catch (IOException e) {
                logSystemSevere("Failed to read matching log: " + e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    // delete a log file (by filename)
    public synchronized boolean deleteLog(String filename) {
        Path p = baseDir.resolve(filename);
        try {
            boolean res = Files.deleteIfExists(p);
            if (res) logSystem("Deleted log " + filename);
            else logSystemWarning("Delete failed (not found): " + filename);
            return res;
        } catch (IOException e) {
            logSystemSevere("Delete error: " + e.getMessage());
            return false;
        }
    }

    // move a log to moved/ subfolder
    public synchronized boolean moveLog(String filename) {
        Path src = baseDir.resolve(filename);
        Path targetDir = baseDir.resolve("moved");
        Path dst = targetDir.resolve(filename);
        try {
            if (!Files.exists(src)) {
                logSystemWarning("Move failed, not found: " + filename);
                return false;
            }
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
            logSystem("Moved log " + filename + " -> moved/" + filename);
            return true;
        } catch (IOException e) {
            logSystemSevere("Move error: " + e.getMessage());
            return false;
        }
    }

    // archive (zip) a set of filenames (relative to baseDir) into archive/<zipname>
    public synchronized Optional<Path> archiveLogs(List<String> filenames, String zipName) {
        if (filenames == null || filenames.isEmpty()) return Optional.empty();
        Path archiveDir = baseDir.resolve("archive");
        try {
            if (!Files.exists(archiveDir)) Files.createDirectories(archiveDir);
            if (zipName == null || zipName.isBlank()) {
                zipName = "logs_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip";
            } else if (!zipName.endsWith(".zip")) zipName += ".zip";
            Path zipPath = archiveDir.resolve(zipName);
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
                for (String fn : filenames) {
                    Path p = baseDir.resolve(fn);
                    if (!Files.exists(p)) continue;
                    ZipEntry entry = new ZipEntry(fn);
                    zos.putNextEntry(entry);
                    Files.copy(p, zos);
                    zos.closeEntry();
                }
            }
            logSystem("Archived logs into " + zipPath.getFileName());
            return Optional.of(zipPath);
        } catch (IOException e) {
            logSystemSevere("Archive error: " + e.getMessage());
            return Optional.empty();
        }
    }

    // close writers & streams
    public synchronized void closeAll() {
        try {
            for (BufferedWriter bw : writers.values()) {
                try { bw.close(); } catch (IOException ignored) {}
            }
            for (FileOutputStream fos : binStreams.values()) {
                try { fos.close(); } catch (IOException ignored) {}
            }
        } finally {
            writers.clear();
            binStreams.clear();
        }
    }
}
